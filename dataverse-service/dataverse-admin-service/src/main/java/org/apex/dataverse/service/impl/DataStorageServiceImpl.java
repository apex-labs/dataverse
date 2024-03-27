package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.service.IDataStorageService;
import org.apex.dataverse.service.IHdfsStorageService;
import org.apex.dataverse.entity.DataStorage;
import org.apex.dataverse.entity.HdfsStorage;
import org.apex.dataverse.entity.JdbcStorage;
import org.apex.dataverse.enums.DataStorageTypeEnum;
import org.apex.dataverse.mapper.DataStorageMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DataStorageParam;
import org.apex.dataverse.param.PageDataStorageParam;
import org.apex.dataverse.service.IJdbcStorageService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.DataStorageVO;
import org.apex.dataverse.vo.HdfsStorageVO;
import org.apex.dataverse.vo.JdbcStorageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class DataStorageServiceImpl extends ServiceImpl<DataStorageMapper, DataStorage> implements IDataStorageService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IHdfsStorageService hdfsStorageService;

    @Autowired
    private IJdbcStorageService jdbcStorageService;


    @Override
    public PageResult<DataStorageVO> pageDataStorage(PageDataStorageParam pageDataStorageParam) throws DtvsAdminException {
        validatePageDataStorage(pageDataStorageParam);
        IPage<DataStorage> iPage = getIPage(pageDataStorageParam);
        PageResult<DataStorageVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DataStorageVO.class));
        buildDataStorageVO(pageResult.getList());
        return pageResult;
    }

    private void buildDataStorageVO(List<DataStorageVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<Long> storageIds = list.stream().map(DataStorageVO::getStorageId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(storageIds)) {
                List<JdbcStorage> jdbcStorageList = jdbcStorageService.list(Wrappers.<JdbcStorage>lambdaQuery().in(JdbcStorage::getStorageId, storageIds));
                List<HdfsStorage> hdfsStorageList = hdfsStorageService.list(Wrappers.<HdfsStorage>lambdaQuery().in(HdfsStorage::getStorageId, storageIds));
                Map<Long, List<JdbcStorageVO>> jdbcStorageMap = null;
                if (CollectionUtil.isNotEmpty(jdbcStorageList)) {
                    List<JdbcStorageVO> jdbcStorageVOList = mapperFactory.getMapperFacade().mapAsList(jdbcStorageList, JdbcStorageVO.class);
                    jdbcStorageMap = jdbcStorageVOList.stream().collect(Collectors.groupingBy(JdbcStorageVO::getStorageId));
                }
                Map<Long, List<HdfsStorageVO>> hdfsStorageMap = null;
                if (CollectionUtil.isNotEmpty(hdfsStorageList)) {
                    List<HdfsStorageVO> hdfsStorageVOList = mapperFactory.getMapperFacade().mapAsList(hdfsStorageList, HdfsStorageVO.class);
                    hdfsStorageMap = hdfsStorageVOList.stream().collect(Collectors.groupingBy(HdfsStorageVO::getStorageId));
                }
                for (DataStorageVO dataStorageVO : list) {
                    if (CollectionUtil.isNotEmpty(jdbcStorageMap)) {
                        if (CollectionUtil.isNotEmpty(jdbcStorageMap.get(dataStorageVO.getStorageId()))) {

                        }
                    }
                    if (CollectionUtil.isNotEmpty(hdfsStorageMap)) {
                        if (CollectionUtil.isNotEmpty(hdfsStorageMap.get(dataStorageVO.getStorageId()))) {
                        }
                    }
                }
            }
        }
    }

    private IPage<DataStorage> getIPage(PageDataStorageParam pageDataStorageParam) {
        //默认单页10条记录
        Page<DataStorage> page = new Page<>();
        PageQueryParam pageQueryParam = pageDataStorageParam.getPageQueryParam();
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

        LambdaQueryWrapper<DataStorage> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(pageDataStorageParam.getStorageTypeId())) {
            queryWrapper.eq(DataStorage::getStorageTypeId, pageDataStorageParam.getStorageTypeId());
        }
        if (StringUtils.isNotEmpty(pageDataStorageParam.getKeyword())) {
            if (pageDataStorageParam.getKeyword().matches("[0-9]+$")) {
                queryWrapper.eq(DataStorage::getStorageId, Long.parseLong(pageDataStorageParam.getKeyword()));
            } else {
                queryWrapper.like(DataStorage::getStorageName, pageDataStorageParam.getKeyword()).or(w -> w.like(DataStorage::getStorageAlias, pageDataStorageParam.getKeyword()));
            }
        }
        return page(page, queryWrapper);
    }

    private void validatePageDataStorage(PageDataStorageParam pageDataStorageParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageDataStorageParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageDataStorageParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException {
        validateAddDataStorage(dataStorageParam);
        validateAddDataStorageDuplicate(dataStorageParam);
        // 新增数据存储区
        Long storageId = saveOrUpdateDataStorage(dataStorageParam);
        // 新增数据存储区配置
        saveDataStorageConf(storageId, dataStorageParam);
        return storageId;
    }

    private void validateAddDataStorageDuplicate(DataStorageParam dataStorageParam) throws DtvsAdminException {
        // 名称唯一判断
        List<DataStorage> dataStorageList = list(Wrappers.<DataStorage>lambdaQuery().eq(DataStorage::getStorageName, dataStorageParam.getStorageName()).
                eq(DataStorage::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(dataStorageList)) {
            throw new DtvsAdminException("数据存储存储名称重复");
        }
    }

    private void saveDataStorageConf(Long storageId, DataStorageParam dataStorageParam) throws DtvsAdminException {
        if (dataStorageParam.getStorageTypeId().equals(DataStorageTypeEnum.HDFS.getValue())) {
            HdfsStorage hdfsStorage = mapperFactory.getMapperFacade().map(dataStorageParam.getDataStorageConfParam().getHdfsStorageParam(), HdfsStorage.class);
            hdfsStorage.setStorageId(storageId);
            if (!hdfsStorageService.saveOrUpdate(hdfsStorage)) {
                throw new DtvsAdminException("保存数据存储HDFS配置失败");
            }
        }
        if (dataStorageParam.getStorageTypeId().equals(DataStorageTypeEnum.MYSQL.getValue())) {
            JdbcStorage jdbcStorage = mapperFactory.getMapperFacade().map(dataStorageParam.getDataStorageConfParam().getJdbcStorageParam(), JdbcStorage.class);
            jdbcStorage.setStorageId(storageId);
            if (!jdbcStorageService.saveOrUpdate(jdbcStorage)) {
                throw new DtvsAdminException("保存数据存储JDBC配置失败");
            }
        }
    }

    private Long saveOrUpdateDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException {
        DataStorage dataStorage = mapperFactory.getMapperFacade().map(dataStorageParam, DataStorage.class);
        if (Objects.isNull(dataStorageParam.getStorageId())) {
            dataStorage.setCreateTime(LocalDateTime.now());
        }
        dataStorage.setUpdateTime(LocalDateTime.now());
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        dataStorage.setTenantId(userInfo.getTenantId());
        dataStorage.setTenantName(userInfo.getTenantName());
        dataStorage.setDeptId(userInfo.getDeptId());
        dataStorage.setDeptName(userInfo.getDeptName());
        dataStorage.setUserId(userInfo.getUserId());
        dataStorage.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(dataStorage)) {
            throw new DtvsAdminException("保存数据存储区失败");
        }
        return dataStorage.getStorageId();
    }

    private void validateAddDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException {
        if (Objects.isNull(dataStorageParam)) {
            throw new DtvsAdminException("数据存储请求参数为空");
        }
        if (StringUtils.isBlank(dataStorageParam.getStorageName())) {
            throw new DtvsAdminException("数据存储存储名称为空");
        }
        if (!dataStorageParam.getStorageName().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据存储存储名称字母开头、包含字母、数字、下划线");
        }
        if (dataStorageParam.getStorageName().length() > 50) {
            throw new DtvsAdminException("数据存储存储名称长度超过50");
        }
        if (StringUtils.isBlank(dataStorageParam.getStorageAbbr())) {
            throw new DtvsAdminException("数据存储存储简称为空");
        }
        if (!dataStorageParam.getStorageAbbr().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据存储存储简称字母开头、包含字母、数字、下划线");
        }
        if (dataStorageParam.getStorageAbbr().length() > 20) {
            throw new DtvsAdminException("数据存储存储简称长度超过20");
        }
        if (StringUtils.isBlank(dataStorageParam.getStorageAlias())) {
            throw new DtvsAdminException("数据存储存储别名为空");
        }
        if (!dataStorageParam.getStorageAlias().matches("[\\u4e00-\\u9fa5]+")) {
            throw new DtvsAdminException("数据存储存储别名中文名称");
        }
        if (dataStorageParam.getStorageAlias().length() > 50) {
            throw new DtvsAdminException("数据存储存储别名长度超过50");
        }
        if (Objects.isNull(dataStorageParam.getStorageTypeId())) {
            throw new DtvsAdminException("数据存储存储类型ID为空");
        }
        if (Objects.isNull(dataStorageParam.getStorageConnType())) {
            throw new DtvsAdminException("数据存储存储类型为空");
        }
        if (Objects.isNull(dataStorageParam.getDataStorageConfParam())) {
            throw new DtvsAdminException("数据存储配置为空");
        }
        validateDataStorageConf(dataStorageParam);
    }

    private void validateDataStorageConf(DataStorageParam dataStorageParam) throws DtvsAdminException {
        if (dataStorageParam.getStorageTypeId().equals(DataStorageTypeEnum.HDFS.getValue())) {
            if (Objects.isNull(dataStorageParam.getDataStorageConfParam().getHdfsStorageParam())) {
                throw new DtvsAdminException("HDFS存储配置为空");
            }
        }
        if (dataStorageParam.getStorageTypeId().equals(DataStorageTypeEnum.MYSQL.getValue())) {
            if (Objects.isNull(dataStorageParam.getDataStorageConfParam().getJdbcStorageParam())) {
                throw new DtvsAdminException("JDBC存储配置为空");
            }
            if (Objects.isNull(dataStorageParam.getDataStorageConfParam().getJdbcStorageParam().getJdbcUrl())) {
                throw new DtvsAdminException("JDBC存储配置JdbcUrl为空");
            }
            if (Objects.isNull(dataStorageParam.getDataStorageConfParam().getJdbcStorageParam().getUserName())) {
                throw new DtvsAdminException("JDBC存储配置用户名为空");
            }
            if (Objects.isNull(dataStorageParam.getDataStorageConfParam().getJdbcStorageParam().getPassword())) {
                throw new DtvsAdminException("JDBC存储配置密码为空");
            }
            if (Objects.isNull(dataStorageParam.getDataStorageConfParam().getJdbcStorageParam().getStorageType())) {
                throw new DtvsAdminException("JDBC存储配置存储类型为空");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException {
        validateEditDataStorage(dataStorageParam);
        // 编辑数据存储区
        Long storageId = saveOrUpdateDataStorage(dataStorageParam);
        // 新增数据存储区配置
        saveDataStorageConf(storageId, dataStorageParam);
        return storageId;
    }

    private void validateEditDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException {
        DataStorage dataStorage;
        validateAddDataStorage(dataStorageParam);
        if (Objects.isNull(dataStorageParam.getStorageId())) {
            throw new DtvsAdminException("数据存储区ID为空");
        }
        if (Objects.isNull(dataStorage = getById(dataStorageParam.getStorageId()))) {
            throw new DtvsAdminException("数据存储区不存在");
        }
        List<DataStorage> dataStorageList = list(Wrappers.<DataStorage>lambdaQuery().eq(DataStorage::getStorageName, dataStorageParam.getStorageName()).
                eq(DataStorage::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(dataStorageList)) {
            if (dataStorageList.stream().filter(d -> !d.getStorageId().equals(dataStorage.getStorageId())).count() > 0) {
                throw new DtvsAdminException("数据存储区名称重复");
            }
        }
        // 清空数据存储区旧的配置
        if (dataStorageParam.getStorageTypeId().equals(DataStorageTypeEnum.HDFS.getValue())) {
            if (CollectionUtil.isNotEmpty(hdfsStorageService.list(Wrappers.<HdfsStorage>lambdaQuery().eq(HdfsStorage::getStorageId, dataStorageParam.getStorageId())))) {
                if (!hdfsStorageService.remove(Wrappers.<HdfsStorage>lambdaQuery().eq(HdfsStorage::getStorageId, dataStorageParam.getStorageId()))) {
                    throw new DtvsAdminException("删除数据存储区HDFS配置失败");
                }
            }
        }
        if (dataStorageParam.getStorageTypeId().equals(DataStorageTypeEnum.MYSQL.getValue())) {
            if (CollectionUtil.isNotEmpty(jdbcStorageService.list(Wrappers.<JdbcStorage>lambdaQuery().eq(JdbcStorage::getStorageId, dataStorageParam.getStorageId())))) {
                if (!jdbcStorageService.remove(Wrappers.<JdbcStorage>lambdaQuery().eq(JdbcStorage::getStorageId, dataStorageParam.getStorageId()))) {
                    throw new DtvsAdminException("删除数据存储区JDBC配置失败");
                }
            }
        }

    }
}
