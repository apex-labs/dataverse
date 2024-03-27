package org.apex.dataverse.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.enums.ConnTypeEnum;
import org.apex.dataverse.enums.EngineTypeEnum;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.enums.StorageTypeEnum;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.feign.admin.AdminFeignClient;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.*;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.entity.OdpcStorage;
import org.apex.dataverse.storage.entity.Storage;
import org.apex.dataverse.storage.entity.StorageTenant;
import org.apex.dataverse.storage.service.IJdbcStorageService;
import org.apex.dataverse.storage.service.IOdpcStorageService;
import org.apex.dataverse.storage.service.IStorageService;
import org.apex.dataverse.storage.service.IStorageTenantService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: StorageDAO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/12 11:48
 */
@Service
public class StorageDAO {

    @Autowired
    private IStorageService storageService;

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IStorageTenantService storageTenantService;

    @Autowired
    private IJdbcStorageService jdbcStorageService;

    @Autowired
    private IOdpcStorageService odpcStorageService;

    @Autowired
    private AdminFeignClient adminFeignClient;

    public PageResult<StorageVO> pageStorage(StoragePageParam storagePageParam) throws DtvsManageException {
        validatePageStorage(storagePageParam);
        IPage<Storage> iPage = getIPage(storagePageParam);
        PageResult<StorageVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), StorageVO.class));
        buildPageStorageVO(pageResult.getList());
        return pageResult;
    }

    private void buildPageStorageVO(List<StorageVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<Long> storageIds = list.stream().map(StorageVO::getStorageId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(storageIds)) {
                List<JdbcStorage> jdbcStorageList = jdbcStorageService.list(Wrappers.<JdbcStorage>lambdaQuery().in(JdbcStorage::getStorageId, storageIds));
                List<OdpcStorage> odpcStorageList = odpcStorageService.list(Wrappers.<OdpcStorage>lambdaQuery().in(OdpcStorage::getStorageId, storageIds));
                if (CollectionUtil.isNotEmpty(jdbcStorageList)) {
                    List<JdbcStorageVO> jdbcStorageVOList = mapperFactory.getMapperFacade().mapAsList(jdbcStorageList, JdbcStorageVO.class);
                    Map<Long, List<JdbcStorageVO>> jdbcStorageVOMap = jdbcStorageVOList.stream().collect(Collectors.groupingBy(j -> j.getStorageId()));
                    if (CollectionUtil.isNotEmpty(jdbcStorageVOMap)) {
                        for (StorageVO storageVO : list) {
                            if (CollectionUtil.isNotEmpty(jdbcStorageVOMap.get(storageVO.getStorageId()))) {
                                storageVO.setJdbcStorageVO(jdbcStorageVOMap.get(storageVO.getStorageId()).get(0));
                            }
                        }
                    }
                }

                if (CollectionUtil.isNotEmpty(odpcStorageList)) {
                    List<OdpcStorageVO> odpcStorageVOList = mapperFactory.getMapperFacade().mapAsList(odpcStorageList, OdpcStorageVO.class);
                    Map<Long, List<OdpcStorageVO>> odpcStorageVOMap = odpcStorageVOList.stream().collect(Collectors.groupingBy(j -> j.getStorageId()));
                    if (CollectionUtil.isNotEmpty(odpcStorageVOMap)) {
                        for (StorageVO storageVO : list) {
                            if (CollectionUtil.isNotEmpty(odpcStorageVOMap.get(storageVO.getStorageId()))) {
                                storageVO.setOdpcStorageVO(odpcStorageVOMap.get(storageVO.getStorageId()).get(0));
                            }
                        }
                    }
                }
            }
        }
    }

    private IPage<Storage> getIPage(StoragePageParam storagePageParam) {
        //默认单页10条记录
        Page<Storage> page = new Page<>();
        PageQueryParam pageQueryParam = storagePageParam.getPageQueryParam();
        if (pageQueryParam != null && pageQueryParam.getSize() != null) {
            page.setSize(pageQueryParam.getSize());
        } else {
            page.setSize(10);
        }

        if (pageQueryParam != null && pageQueryParam.getPageNo() != null) {
            page.setCurrent(pageQueryParam.getPageNo());
        } else {
            page.setCurrent(1);
        }
        if (pageQueryParam != null) {
            if (pageQueryParam.getDescs() != null) {
                page.setDescs(Optional.of(pageQueryParam.getDescs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
            if (pageQueryParam.getAscs() != null) {
                page.setAscs(Optional.of(pageQueryParam.getAscs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
        }

        LambdaQueryWrapper<Storage> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(storagePageParam.getStorageName())) {
            queryWrapper.like(Storage::getStorageName, storagePageParam.getStorageName());
        }
        if (Objects.nonNull(storagePageParam.getStorageType())) {
            queryWrapper.eq(Storage::getStorageType, storagePageParam.getStorageType());
        }
        if (Objects.nonNull(storagePageParam.getStorageType())) {
            queryWrapper.eq(Storage::getStorageType, storagePageParam.getStorageType());
        }
        if (Objects.nonNull(storagePageParam.getStorageType())) {
            queryWrapper.eq(Storage::getStorageType, storagePageParam.getStorageType());
        }
        queryWrapper.eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue());

        return storageService.page(page, queryWrapper);
    }

    private void validatePageStorage(StoragePageParam storagePageParam) throws DtvsManageException {
        PageQueryParam pageQueryParam = storagePageParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsManageException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        storagePageParam.setPageQueryParam(pageQueryParam);
    }

    public Long add(SaveStorageParam saveStorageParam) throws DtvsManageException {
        validateSaveOrUpdateStorage(saveStorageParam);
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        Storage storage = mapperFactory.getMapperFacade().map(saveStorageParam, Storage.class);
        storage.setCreateTime(LocalDateTime.now());
        storage.setUpdateTime(LocalDateTime.now());
        storage.setCreatorId(userInfo.getUserId());
        storage.setCreatorName(userInfo.getUserName());
        storage.setIsDeleted(IsDeletedEnum.NO.getValue());
        if (!storageService.saveOrUpdate(storage)) {
            throw new DtvsManageException("保存存储区失败");
        }
        if (Objects.nonNull(saveStorageParam.getSaveJdbcStorageParam())) {
            JdbcStorage jdbcStorage = mapperFactory.getMapperFacade().map(saveStorageParam.getSaveJdbcStorageParam(), JdbcStorage.class);
            jdbcStorage.setStorageId(storage.getStorageId());
            jdbcStorage.setCreateTime(LocalDateTime.now());
            jdbcStorage.setUpdateTime(LocalDateTime.now());
            if (!jdbcStorageService.saveOrUpdate(jdbcStorage)) {
                throw new DtvsManageException("保存JDBC存储失败");
            }
        }
        if (Objects.nonNull(saveStorageParam.getSaveOdpcStorageParam())) {
            OdpcStorage odpcStorage = mapperFactory.getMapperFacade().map(saveStorageParam.getSaveOdpcStorageParam(), OdpcStorage.class);
            odpcStorage.setStorageId(storage.getStorageId());
            odpcStorage.setCreateTime(LocalDateTime.now());
            odpcStorage.setUpdateTime(LocalDateTime.now());
            if (!odpcStorageService.saveOrUpdate(odpcStorage)) {
                throw new DtvsManageException("保存ODPC存储失败");
            }
        }
        return storage.getStorageId();
    }

    private void validateSaveOrUpdateStorage(SaveStorageParam saveStorageParam) throws DtvsManageException {
        if (Objects.isNull(saveStorageParam)) {
            throw new DtvsManageException("参数为空");
        }
        if (StringUtils.isBlank(saveStorageParam.getStorageName())) {
            throw new DtvsManageException("存储名称参数为空");
        }
        if (!saveStorageParam.getStorageName().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsManageException("存储名称只能为英文");
        }
        if (saveStorageParam.getStorageName().length() > 50) {
            throw new DtvsManageException("存储名称不能超过最大长度");
        }
        if (StringUtils.isBlank(saveStorageParam.getStorageAlias())) {
            throw new DtvsManageException("存储名称别名参数为空");
        }
        if (saveStorageParam.getStorageAlias().length() > 50) {
            throw new DtvsManageException("存储名称别名不能超过最大长度");
        }
        if (StringUtils.isBlank(saveStorageParam.getStorageAbbr())) {
            throw new DtvsManageException("存储名称简称参数为空");
        }
        if (!saveStorageParam.getStorageAbbr().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsManageException("存储名称简称只能为英文");
        }
        if (saveStorageParam.getStorageAbbr().length() > 20) {
            throw new DtvsManageException("存储名称简称不能超过最大长度");
        }
        if (StringUtils.isBlank(saveStorageParam.getStorageType())) {
            throw new DtvsManageException("存储类型参数为空");
        }
        if (!StorageTypeEnum.existsValue(saveStorageParam.getStorageType())) {
            throw new DtvsManageException("存储类型不存在,请选择正确的存储类型");
        }
        if (StringUtils.isBlank(saveStorageParam.getEngineType())) {
            throw new DtvsManageException("引擎类型参数为空");
        }
        if (!EngineTypeEnum.existsValue(saveStorageParam.getEngineType())) {
            throw new DtvsManageException("引擎类型不存在,请选择正确的引擎类型");
        }
        if (StringUtils.isBlank(saveStorageParam.getConnType())) {
            throw new DtvsManageException("连接类型参数为空");
        }
        if (!ConnTypeEnum.existsValue(saveStorageParam.getConnType())) {
            throw new DtvsManageException("连接类型不存在,请选择正确的连接类型");
        }
        if (StringUtils.isNoneBlank(saveStorageParam.getDescription()) && saveStorageParam.getDescription().length() > 255) {
            throw new DtvsManageException("存储区描述超过最大长度");
        }
        if (Objects.isNull(saveStorageParam.getSaveJdbcStorageParam()) && Objects.isNull(saveStorageParam.getSaveOdpcStorageParam())) {
            throw new DtvsManageException("JDBC存储区和ODPC存储区不能同时为空");
        }

        // 校验jdbc/odpc存储参数
        validateSaveJdbcStorage(saveStorageParam.getSaveJdbcStorageParam());
        validateSaveOdpcStorage(saveStorageParam.getSaveOdpcStorageParam());

        Storage nameStorage = storageService.getOne(Wrappers.<Storage>lambdaQuery().eq(Storage::getStorageName,saveStorageParam.getStorageName()).eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue()));
        Storage aliasStorage = storageService.getOne(Wrappers.<Storage>lambdaQuery().eq(Storage::getStorageAlias,saveStorageParam.getStorageAlias()).eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue()));
        Storage abbrStorage = storageService.getOne(Wrappers.<Storage>lambdaQuery().eq(Storage::getStorageAbbr,saveStorageParam.getStorageAbbr()).eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue()));

        // 新增存储区判断
        if (Objects.isNull(saveStorageParam.getStorageId())) {
            if (Objects.nonNull(nameStorage)) {
                throw new DtvsManageException("存储区名称已存在");
            }
            if (Objects.nonNull(aliasStorage)) {
                throw new DtvsManageException("存储区别名已存在");
            }
            if (Objects.nonNull(abbrStorage)) {
                throw new DtvsManageException("存储区简称已存在");
            }
        }

        // 更新存储区
        if (Objects.nonNull(saveStorageParam.getStorageId())) {
            Storage storage = storageService.getById(saveStorageParam.getStorageId());
            if (Objects.isNull(storage)) {
                throw new DtvsManageException("存储区不存在");
            } else {
                if (storage.getIsDeleted().intValue() == IsDeletedEnum.YES.getValue()) {
                    throw new DtvsManageException("存储区已被删除");
                }
                // todo 当存储区被使用时不允许编辑
                List<StorageBoxFeignVO> storageBoxFeignVOList = adminFeignClient.listStorageBoxByStorageId(saveStorageParam.getStorageId());
                if (CollectionUtil.isNotEmpty(storageBoxFeignVOList)) {
                    throw new DtvsManageException("存储区已经被使用不允许编辑");
                }

                if (Objects.nonNull(nameStorage) && storage.getStorageId().longValue() != nameStorage.getStorageId().longValue() && storage.getStorageName().equals(nameStorage.getStorageName())) {
                    throw new DtvsManageException("存储区名称重复");
                }
                if (Objects.nonNull(aliasStorage) && storage.getStorageId().longValue() != aliasStorage.getStorageId().longValue() && storage.getStorageName().equals(aliasStorage.getStorageAlias())) {
                    throw new DtvsManageException("存储区别名重复");
                }
                if (Objects.nonNull(abbrStorage) && storage.getStorageId().longValue() != abbrStorage.getStorageId().longValue() && storage.getStorageName().equals(abbrStorage.getStorageAbbr())) {
                    throw new DtvsManageException("存储区名称简称重复");
                }
                // 清除旧的存储关系
                if (CollectionUtil.isNotEmpty(jdbcStorageService.list(Wrappers.<JdbcStorage>lambdaQuery().eq(JdbcStorage::getStorageId, saveStorageParam.getStorageId())))) {
                    if (!jdbcStorageService.remove(Wrappers.<JdbcStorage>lambdaQuery().eq(JdbcStorage::getStorageId, saveStorageParam.getStorageId()))) {
                        throw new DtvsManageException("删除旧的JDBC存储失败");
                    }
                }
                if (CollectionUtil.isNotEmpty(odpcStorageService.list(Wrappers.<OdpcStorage>lambdaQuery().eq(OdpcStorage::getStorageId, saveStorageParam.getStorageId())))) {
                    if (!odpcStorageService.remove(Wrappers.<OdpcStorage>lambdaQuery().eq(OdpcStorage::getStorageId, saveStorageParam.getStorageId()))) {
                        throw new DtvsManageException("删除旧的ODPC存储失败");
                    }
                }
            }

        }
    }

    private void validateSaveOdpcStorage(SaveOdpcStorageParam saveOdpcStorageParam) throws DtvsManageException {
        if (Objects.nonNull(saveOdpcStorageParam)) {
            if (StringUtils.isBlank(saveOdpcStorageParam.getStorageType())) {
                throw new DtvsManageException("ODPC存储参数存储类型为空");
            }
            if (saveOdpcStorageParam.getStorageType().length() > 20) {
                throw new DtvsManageException("ODPC存储参数存储类型超过最大长度");
            }
            if (!StorageTypeEnum.existsValue(saveOdpcStorageParam.getStorageType())) {
                throw new DtvsManageException("ODPC存储参数存储类型不存在,请选择正确的存储类型");
            }
            if (StringUtils.isBlank(saveOdpcStorageParam.getStoragePath())) {
                throw new DtvsManageException("ODPC存储区参数存储路径为空");
            }
            if (saveOdpcStorageParam.getStoragePath().length() > 255) {
                throw new DtvsManageException("ODPC存储区参数存储路径超过最大长度");
            }
        }
    }

    private void validateSaveJdbcStorage(SaveJdbcStorageParam saveJdbcStorageParam) throws DtvsManageException {
        if (Objects.nonNull(saveJdbcStorageParam)) {
            if (StringUtils.isBlank(saveJdbcStorageParam.getStorageType())) {
                throw new DtvsManageException("JDBC存储参数存储类型为空");
            }
            if (saveJdbcStorageParam.getStorageType().length() > 20) {
                throw new DtvsManageException("JDBC存储参数存储类型超过最大长度");
            }
            if (!StorageTypeEnum.existsValue(saveJdbcStorageParam.getStorageType())) {
                throw new DtvsManageException("JDBC存储参数存储类型不存在,请选择正确的存储类型");
            }
            if (StringUtils.isBlank(saveJdbcStorageParam.getJdbcUrl())) {
                throw new DtvsManageException("JDBC存储参数JdbcUrl为空");
            }
            if (saveJdbcStorageParam.getJdbcUrl().length() > 255) {
                throw new DtvsManageException("JDBC存储参数JdbcUrl超过最大长度");
            }
            if (StringUtils.isBlank(saveJdbcStorageParam.getUserName())) {
                throw new DtvsManageException("JDBC存储参数UserName为空");
            }
            if (saveJdbcStorageParam.getUserName().length() > 50) {
                throw new DtvsManageException("JDBC存储参数UserName超过最大长度");
            }
            if (StringUtils.isBlank(saveJdbcStorageParam.getPassword())) {
                throw new DtvsManageException("JDBC存储参数Password为空");
            }
            if (saveJdbcStorageParam.getPassword().length() > 255) {
                throw new DtvsManageException("JDBC存储参数Password超过最大长度");
            }
            if (StringUtils.isBlank(saveJdbcStorageParam.getDriverClass())) {
                throw new DtvsManageException("JDBC存储参数DriverClass为空");
            }
            if (saveJdbcStorageParam.getDriverClass().length() > 50) {
                throw new DtvsManageException("JDBC存储参数DriverClass超过最大长度");
            }
            if (StringUtils.isNotBlank(saveJdbcStorageParam.getDescription()) && saveJdbcStorageParam.getDescription().length() > 255) {
                throw new DtvsManageException("JDBC存储参数描述超过最大长度");
            }
        }
    }

    public Long edit(SaveStorageParam saveStorageParam) throws DtvsManageException {
        validateSaveOrUpdateStorage(saveStorageParam);
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        Storage storage = mapperFactory.getMapperFacade().map(saveStorageParam, Storage.class);
        storage.setUpdateTime(LocalDateTime.now());
        storage.setCreatorId(userInfo.getUserId());
        storage.setCreatorName(userInfo.getUserName());
        if (!storageService.saveOrUpdate(storage)) {
            throw new DtvsManageException("保存存储区失败");
        }
        if (Objects.nonNull(saveStorageParam.getSaveJdbcStorageParam())) {
            JdbcStorage jdbcStorage = mapperFactory.getMapperFacade().map(saveStorageParam.getSaveJdbcStorageParam(), JdbcStorage.class);
            jdbcStorage.setStorageId(storage.getStorageId());
            jdbcStorage.setCreateTime(LocalDateTime.now());
            jdbcStorage.setUpdateTime(LocalDateTime.now());
            if (!jdbcStorageService.saveOrUpdate(jdbcStorage)) {
                throw new DtvsManageException("保存JDBC存储失败");
            }
        }
        if (Objects.nonNull(saveStorageParam.getSaveOdpcStorageParam())) {
            OdpcStorage odpcStorage = mapperFactory.getMapperFacade().map(saveStorageParam.getSaveOdpcStorageParam(), OdpcStorage.class);
            odpcStorage.setStorageId(storage.getStorageId());
            odpcStorage.setCreateTime(LocalDateTime.now());
            odpcStorage.setUpdateTime(LocalDateTime.now());
            if (!odpcStorageService.saveOrUpdate(odpcStorage)) {
                throw new DtvsManageException("保存ODPC存储失败");
            }
        }
        return storage.getStorageId();
    }

    public Boolean attachStorageTenant(List<AttachStorageTenantParam> attachStorageTenantParamList) throws DtvsManageException {
        if (CollectionUtil.isEmpty(attachStorageTenantParamList)) {
            throw new DtvsManageException("参数为空");
        }
        List<Long> storageIds = attachStorageTenantParamList.stream().map(AttachStorageTenantParam::getStorageId).collect(Collectors.toList());
        List<Long> tenantIds = attachStorageTenantParamList.stream().map(AttachStorageTenantParam::getTenantId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(storageIds)) {
            throw new DtvsManageException("存储区ID为空");
        }
        if (CollectionUtil.isEmpty(tenantIds)) {
            throw new DtvsManageException("租户ID为空");
        }
        return null;
    }

    /**
     * todo 分配租户暂时通过数据库脚本初始化
     * @return
     */
    public List<StorageTenantVO> tenants() {
        return mapperFactory.getMapperFacade().mapAsList(storageTenantService.list(), StorageTenantVO.class);
    }

    public List<EnumInfoVO> storageTypeList() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (StorageTypeEnum storageTypeEnum : StorageTypeEnum.values())  {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(storageTypeEnum.getValue());
            enumInfoVO.setDesc(storageTypeEnum.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }

        return enumInfoVOList;
    }

    public List<EnumInfoVO> engineTypeList() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (EngineTypeEnum engineTypeEnum : EngineTypeEnum.values())  {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(engineTypeEnum.getValue());
            enumInfoVO.setDesc(engineTypeEnum.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }

        return enumInfoVOList;
    }

    public List<EnumInfoVO> connTypeList() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (ConnTypeEnum connTypeEnum : ConnTypeEnum.values())  {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(connTypeEnum.getValue());
            enumInfoVO.setDesc(connTypeEnum.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }

        return enumInfoVOList;
    }

    public List<StorageVO> listStorageVO(ListStorageParam listStorageParam) {
        LambdaQueryWrapper<Storage> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(listStorageParam)) {
            if (CollectionUtil.isNotEmpty(listStorageParam.getStorageIds())) {
                queryWrapper.in(Storage::getStorageId, listStorageParam.getStorageIds());
            }
            if (StringUtils.isNoneBlank(listStorageParam.getStorageName())) {
                queryWrapper.eq(Storage::getStorageName, listStorageParam.getStorageName());
            }
            if (StringUtils.isNoneBlank(listStorageParam.getStorageType())) {
                queryWrapper.eq(Storage::getStorageType, listStorageParam.getStorageType());
            }
            if (StringUtils.isNoneBlank(listStorageParam.getConnType())) {
                queryWrapper.eq(Storage::getConnType, listStorageParam.getConnType());
            }
            queryWrapper.eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue());
        }
        return mapperFactory.getMapperFacade().mapAsList(storageService.list(queryWrapper), StorageVO.class);
    }

    /**
     * 查询存储区详情
     * @param storageId
     * @return
     */
    public StorageVO detail(Long storageId) throws DtvsManageException {
        Storage storage;
        JdbcStorageVO jdbcStorageVO;
        OdpcStorageVO odpcStorageVO;

        if (Objects.isNull(storageId)) {
            throw new DtvsManageException("参数为空");
        }
        if (Objects.isNull(storage = storageService.getOne(Wrappers.<Storage>lambdaQuery().eq(Storage::getStorageId, storageId).eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsManageException("存储区不存在");
        }

        StorageVO storageVO = mapperFactory.getMapperFacade().map(storage, StorageVO.class);
        if (Objects.nonNull(jdbcStorageVO = mapperFactory.getMapperFacade().map(jdbcStorageService.getByStorageId(storageId), JdbcStorageVO.class))) {
            storageVO.setJdbcStorageVO(jdbcStorageVO);
        }
        if (Objects.nonNull(odpcStorageVO = mapperFactory.getMapperFacade().map(odpcStorageService.getByStorageId(storageId), OdpcStorageVO.class))) {
            storageVO.setOdpcStorageVO(odpcStorageVO);
        }

        return storageVO;
    }

    public Boolean delete(Long storageId) throws DtvsManageException {
        Storage storage;
        JdbcStorageVO jdbcStorageVO;
        OdpcStorageVO odpcStorageVO;

        if (Objects.isNull(storageId)) {
            throw new DtvsManageException("参数为空");
        }
        if (Objects.isNull(storage = storageService.getOne(Wrappers.<Storage>lambdaQuery().eq(Storage::getStorageId, storageId).eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsManageException("存储区不存在");
        }
        List<StorageBoxFeignVO> storageBoxFeignVOList = adminFeignClient.listStorageBoxByStorageId(storageId);
        if (CollectionUtil.isNotEmpty(storageBoxFeignVOList)) {
            throw new DtvsManageException("存储区已经被使用不允许编辑");
        }

        return Boolean.TRUE;
    }
}
