package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.constrant.SqlGrammarConst;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.*;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.manage.ManageFeignClient;
import org.apex.dataverse.mapper.DvsParentMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.*;
import org.apex.dataverse.service.*;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.UCodeUtil;
import org.apex.dataverse.vo.*;
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
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Service
public class DvsParentServiceImpl extends ServiceImpl<DvsParentMapper, DvsParent> implements IDvsParentService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IDvsService dvsService;

    @Autowired
    private IDvsMemberService dvsMemberService;

    @Autowired
    private IStorageBoxService storageBoxService;

    @Autowired
    private IEtlJobService etlJobService;

    @Autowired
    private IBdmJobService bdmJobService;

    @Autowired
    private IDvsTableService dvsTableService;

    @Autowired
    private ManageFeignClient manageFeignClient;

    @Override
    public PageResult<DvsParentVO> pageDvsParent(PageDvsParentParam pageDvsParentParam) throws DtvsAdminException {
        validateDvsParent(pageDvsParentParam);
        IPage<DvsParent> iPage = getIPage(pageDvsParentParam);
        PageResult<DvsParentVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DvsParentVO.class));
        buildDvsParentVO(pageResult.getList());
        return pageResult;
    }

    private void buildDvsParentVO(List<DvsParentVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> dvsCodeList = list.stream().map(DvsParentVO::getDvsCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(dvsCodeList)) {
                List<DvsVO> dvsList = mapperFactory.getMapperFacade().mapAsList(dvsService.list(Wrappers.<Dvs>lambdaQuery().in(Dvs::getDvsCode, dvsCodeList)), DvsVO.class);
                List<DvsMemberVO> dvsMemberList = mapperFactory.getMapperFacade().mapAsList(dvsMemberService.list(Wrappers.<DvsMember>lambdaQuery().in(DvsMember::getDvsCode, dvsCodeList)), DvsMemberVO.class);
                List<EtlJob> etlJobList = etlJobService.list(Wrappers.<EtlJob>lambdaQuery().in(EtlJob::getDvsCode, dvsCodeList));
                List<BdmJob> bdmJobList = bdmJobService.list(Wrappers.<BdmJob>lambdaQuery().in(BdmJob::getDvsCode, dvsCodeList));
                List<DvsTable> dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getDvsCode, dvsCodeList));
                List<StorageBoxVO> storageBoxVOList = mapperFactory.getMapperFacade().mapAsList(storageBoxService.list(Wrappers.<StorageBox>lambdaQuery().in(StorageBox::getDvsCode, dvsCodeList)), StorageBoxVO.class);

                if (CollectionUtil.isNotEmpty(storageBoxVOList)) {
                    Map<String, List<StorageBoxVO>> dvsMap = storageBoxVOList.stream().collect(Collectors.groupingBy(StorageBoxVO::getDvsCode));
                    for (DvsParentVO dvsParentVO : list) {
                        dvsParentVO.setStorageBoxVOList(dvsMap.get(dvsParentVO.getDvsCode()));
                    }
                }

                if (CollectionUtil.isNotEmpty(dvsList)) {
                    Map<String, List<DvsVO>> dvsMap = dvsList.stream().collect(Collectors.groupingBy(DvsVO::getDvsCode));
                    for (DvsParentVO dvsParentVO : list) {
                        dvsParentVO.setDvsVOList(dvsMap.get(dvsParentVO.getDvsCode()));
                    }
                }
                if (CollectionUtil.isNotEmpty(dvsMemberList)) {
                    Map<String, List<DvsMemberVO>> dvsMemberMap = dvsMemberList.stream().collect(Collectors.groupingBy(DvsMemberVO::getDvsCode));
                    for (DvsParentVO dvsParentVO : list) {
                        dvsParentVO.setDvsMemberVOList(dvsMemberMap.get(dvsParentVO.getDvsCode()));
                    }
                }
                for (DvsParentVO dvsParentVO : list) {
                    DvsDetailCountVO dvsDetailCountVO = new DvsDetailCountVO();
                    if (CollectionUtil.isNotEmpty(etlJobList)) {
                        Map<Integer, List<EtlJob>> etlJobMap = etlJobList.stream().collect(Collectors.groupingBy(EtlJob::getEnv));
                        if (CollectionUtil.isNotEmpty(etlJobMap)) {
                            List<EtlJob> etlJobs;
                            if (CollectionUtil.isNotEmpty(etlJobs = etlJobMap.get(EnvEnum.BAISC.getValue()))) {
                                int etlCount = (int) etlJobs.stream().filter(e -> e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                int etlFailCount = (int) etlJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue()) && e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setBasicEtlJobCount(etlCount);
                                dvsDetailCountVO.setBasicEtlJobFailCount(etlFailCount);
                            }
                            if (CollectionUtil.isNotEmpty(etlJobs = etlJobMap.get(EnvEnum.DEV.getValue()))) {
                                int etlCount = (int) etlJobs.stream().filter(e -> e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                int etlFailCount = (int) etlJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue()) && e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setDevEtlJobCount(etlCount);
                                dvsDetailCountVO.setDevEtlJobFailCount(etlFailCount);
                            }
                            if (CollectionUtil.isNotEmpty(etlJobs = etlJobMap.get(EnvEnum.PROD.getValue()))) {
                                int etlCount = (int) etlJobs.stream().filter(e -> e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                int etlFailCount = (int) etlJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue()) && e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setProdEtlJobCount(etlCount);
                                dvsDetailCountVO.setProdEtlJobFailCount(etlFailCount);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(bdmJobList)) {
                        Map<Integer, List<BdmJob>> bdmJobMap = bdmJobList.stream().collect(Collectors.groupingBy(BdmJob::getEnv));
                        if (CollectionUtil.isNotEmpty(bdmJobMap)) {
                            List<BdmJob> bdmJobs;
                            if (CollectionUtil.isNotEmpty(bdmJobs = bdmJobMap.get(EnvEnum.BAISC.getValue()))) {
                                int bdmCount = (int) bdmJobs.stream().filter(e -> e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                int bdmFailCount = (int) bdmJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue()) && e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setBasicBdmJobCount(bdmCount);
                                dvsDetailCountVO.setBasicBdmJobFailCount(bdmFailCount);
                            }
                            if (CollectionUtil.isNotEmpty(bdmJobs = bdmJobMap.get(EnvEnum.DEV.getValue()))) {
                                int bdmCount = (int) bdmJobs.stream().filter(e -> e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                int bdmFailCount = (int) bdmJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue()) && e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setDevBdmJobCount(bdmCount);
                                dvsDetailCountVO.setDevBdmJobFailCount(bdmFailCount);
                            }
                            if (CollectionUtil.isNotEmpty(bdmJobs = bdmJobMap.get(EnvEnum.PROD.getValue()))) {
                                int bdmCount = (int) bdmJobs.stream().filter(e -> e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                int bdmFailCount = (int) bdmJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue()) && e.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setProdBdmJobCount(bdmCount);
                                dvsDetailCountVO.setProdBdmJobFailCount(bdmFailCount);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(dvsTableList)) {
                        Map<Integer, List<DvsTable>> dvsTableMap = dvsTableList.stream().collect(Collectors.groupingBy(DvsTable::getEnv));
                        if (CollectionUtil.isNotEmpty(dvsTableMap)) {
                            List<DvsTable> dvsTables;
                            if (CollectionUtil.isNotEmpty(dvsTables = dvsTableMap.get(EnvEnum.BAISC.getValue()))) {
                                int tableCount = (int) dvsTables.stream().filter(d -> d.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setBasicTableCount(tableCount);
                            }
                            if (CollectionUtil.isNotEmpty(dvsTables = dvsTableMap.get(EnvEnum.DEV.getValue()))) {
                                int tableCount = (int) dvsTables.stream().filter(d -> d.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setDevTableCount(tableCount);
                            }
                            if (CollectionUtil.isNotEmpty(dvsTables = dvsTableMap.get(EnvEnum.PROD.getValue()))) {
                                int tableCount = (int) dvsTables.stream().filter(d -> d.getDvsCode().equals(dvsParentVO.getDvsCode())).count();
                                dvsDetailCountVO.setProdTableCount(tableCount);
                            }
                        }
                    }

                    dvsParentVO.setDvsDetailCountVO(dvsDetailCountVO);
                }
            }
        }
    }

    private IPage<DvsParent> getIPage(PageDvsParentParam pageDvsParentParam) {
        //默认单页10条记录
        Page<DvsParent> page = new Page<>();
        PageQueryParam pageQueryParam = pageDvsParentParam.getPageQueryParam();
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

        LambdaQueryWrapper<DvsParent> queryWrapper = Wrappers.lambdaQuery();

        if (StringUtils.isNotEmpty(pageDvsParentParam.getKeyword())) {
            if (pageDvsParentParam.getKeyword().matches("[0-9]+$")) {
                queryWrapper.eq(DvsParent::getParentId, Long.parseLong(pageDvsParentParam.getKeyword()));
            } else {
                queryWrapper.like(DvsParent::getParentName, pageDvsParentParam.getKeyword())
                        .or(w -> w.like(DvsParent::getParentAlias, pageDvsParentParam.getKeyword()))
                        .or(w -> w.like(DvsParent::getParentAbbr, pageDvsParentParam.getKeyword()));
            }
        }
        queryWrapper.eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue());
        return page(page, queryWrapper);
    }

    private void validateDvsParent(PageDvsParentParam pageDvsParentParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageDvsParentParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageDvsParentParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addDvsParent(DvsParentParam dvsParentParam) throws DtvsAdminException {
        // 获取用户信息 需要controller层才能使用
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateAddDvsParent(dvsParentParam);
        validateAddDvsParentDuplicate(dvsParentParam, userInfo);
        // 新增数据空间父类
        dvsParentParam.setDvsCode(UCodeUtil.produce());
        Long parentId = saveOrUpdateDvsParent(dvsParentParam, userInfo);
        // 新增数据空间
        saveDvs(dvsParentParam, userInfo);
        // 新增数据存储桶
        saveStorageBox(dvsParentParam, userInfo);
        return parentId;
    }

    private void saveStorageBox(DvsParentParam dvsParentParam, UserInfo userInfo) throws DtvsAdminException {
        /**
         * 单存储区，三个层（ods, dw, ads)，都在一个存储区上（三个存储区中的一个）
         * 多存储区，三个层中Ads可以在一个存储区上（三个存储区中的一个）， 非ADS层（ods, dw)在另一个存储区上
         */
        String odsBoxCode = UCodeUtil.produce();
        String dwBoxCode = UCodeUtil.produce();
        String adsBoxCode = UCodeUtil.produce();
//        dvsParentParam.getStorageBoxParamList().forEach(d -> {
//            String[] sPaths = d.getStoragePath().split(SqlGrammarConst.SLASH);
//            String storagePath = String.join(SqlGrammarConst.SLASH, Arrays.asList(sPaths));
//            String boxName = storagePath + SqlGrammarConst.SLASH +
//                    userInfo.getTenantId() + SqlGrammarConst.SLASH +
//                    dvsParentParam.getParentName() + SqlGrammarConst.SLASH +
//                    EnvEnum.getEnvDescByValue(d.getEnv()) + SqlGrammarConst.SLASH +
//                    DwLayerEnum.getDwLayerDescByModel(d.getDwLayer());
//            d.setBoxName(boxName.toLowerCase());
//        });
        LocalDateTime dateTime = LocalDateTime.now();
        String dvsCode = dvsParentParam.getDvsCode();
        List<StorageBox> storageBoxList = new ArrayList<>();
        dvsParentParam.getStorageBoxParamList().forEach(d -> {
            String[] sPaths = d.getStoragePath().split(SqlGrammarConst.SLASH);
            String storagePath = String.join(SqlGrammarConst.SLASH, Arrays.asList(sPaths));
            String boxNamePrefix = storagePath + SqlGrammarConst.SLASH +
                    userInfo.getTenantId() + SqlGrammarConst.SLASH +
                    dvsParentParam.getParentName() + SqlGrammarConst.SLASH;
            if (dvsParentParam.getEnvMode().intValue() == EnvModelEnum.BAISC.getValue()) {
                StorageBox odsStorage = buildStorageBox(d, DwLayerEnum.ODS.getValue(), EnvEnum.BAISC.getValue(), odsBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox dwStorage = buildStorageBox(d, DwLayerEnum.DW.getValue(), EnvEnum.BAISC.getValue(), dwBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox adsStorage = buildStorageBox(d, DwLayerEnum.ADS.getValue(), EnvEnum.BAISC.getValue(), adsBoxCode, dvsCode, dateTime, boxNamePrefix);
                storageBoxList.add(odsStorage);
                storageBoxList.add(dwStorage);
                storageBoxList.add(adsStorage);
            } else {
                StorageBox odsStorage = buildStorageBox(d, DwLayerEnum.ODS.getValue(), EnvEnum.DEV.getValue(), odsBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox dwStorage = buildStorageBox(d, DwLayerEnum.DW.getValue(), EnvEnum.DEV.getValue(), dwBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox adsStorage = buildStorageBox(d, DwLayerEnum.ADS.getValue(), EnvEnum.DEV.getValue(), adsBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox prodDdsStorage = buildStorageBox(d, DwLayerEnum.ODS.getValue(), EnvEnum.PROD.getValue(), odsBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox prodDwStorage = buildStorageBox(d, DwLayerEnum.DW.getValue(), EnvEnum.PROD.getValue(), dwBoxCode, dvsCode, dateTime, boxNamePrefix);
                StorageBox prodAdsStorage = buildStorageBox(d, DwLayerEnum.ADS.getValue(), EnvEnum.PROD.getValue(), adsBoxCode, dvsCode, dateTime, boxNamePrefix);
                storageBoxList.add(odsStorage);
                storageBoxList.add(dwStorage);
                storageBoxList.add(adsStorage);
                storageBoxList.add(prodDdsStorage);
                storageBoxList.add(prodDwStorage);
                storageBoxList.add(prodAdsStorage);
            }
        });
        if (!storageBoxService.saveOrUpdateBatch(storageBoxList)) {
            throw new DtvsAdminException("保存数据存储桶失败");
        }
    }

    private StorageBox buildStorageBox(StorageBoxParam storageBoxParam, int layer, int env, String odsBoxCode, String dvsCode, LocalDateTime dateTime, String boxNamePrefix) {
        String boxName = boxNamePrefix + EnvEnum.getEnvDescByValue(env) + SqlGrammarConst.SLASH +
                DwLayerEnum.getDwLayerDescByModel(layer);
        storageBoxParam.setBoxName(boxName.toLowerCase());
        StorageBox storageBox = mapperFactory.getMapperFacade().map(storageBoxParam, StorageBox.class);
        storageBox.setDwLayer(layer);
        storageBox.setBoxCode(odsBoxCode);
        storageBox.setDvsCode(dvsCode);
        storageBox.setCreateTime(dateTime);
        storageBox.setUpdateTime(dateTime);
        storageBox.setEnv(env);
        return storageBox;
    }

    private void validateAddDvsParentDuplicate(DvsParentParam DvsParentParam, UserInfo userInfo) throws DtvsAdminException {
        // 检查数据空间名称 数据空间简称 数据空间别名是否重复
        // 保证租户内唯一
        List<DvsParent> DvsParents = list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentName, DvsParentParam.getParentName()).
                eq(DvsParent::getTenantId, userInfo.getTenantId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(DvsParents)) {
            throw new DtvsAdminException("数据空间名称重复");
        }

        List<DvsParent> DvsParents2 = list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentAlias, DvsParentParam.getParentName()).
                eq(DvsParent::getTenantId, userInfo.getTenantId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(DvsParents2)) {
            throw new DtvsAdminException("数据空间别名重复");
        }

        List<DvsParent> DvsParents3 = list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentAbbr, DvsParentParam.getParentName()).
                eq(DvsParent::getTenantId, userInfo.getTenantId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(DvsParents3)) {
            throw new DtvsAdminException("数据空间简称重复");
        }
    }

    private void saveDvs(DvsParentParam dvsParentParam, UserInfo userInfo) throws DtvsAdminException {
        if (CollectionUtil.isNotEmpty(dvsParentParam.getDvsParamList())) {
            List<Dvs> dvsList = mapperFactory.getMapperFacade().mapAsList(dvsParentParam.getDvsParamList(), Dvs.class);
            dvsList.forEach(d -> {
                d.setDvsCode(dvsParentParam.getDvsCode());
                d.setIsDeleted(IsDeletedEnum.NO.getValue());
                d.setCreateTime(LocalDateTime.now());
                d.setUpdateTime(LocalDateTime.now());
                d.setTenantId(userInfo.getTenantId());
                d.setTenantName(userInfo.getTenantName());
                d.setDeptId(userInfo.getDeptId());
                d.setDeptName(userInfo.getDeptName());
                d.setUserId(userInfo.getUserId());
                d.setUserName(userInfo.getUserName());
            });
            if (!dvsService.saveOrUpdateBatch(dvsList)) {
                throw new DtvsAdminException("批量保存数据空间失败");
            }
        }

    }

    private Long saveOrUpdateDvsParent(DvsParentParam dvsParentParam, UserInfo userInfo) throws DtvsAdminException {
        DvsParent dvsParent = mapperFactory.getMapperFacade().map(dvsParentParam, DvsParent.class);
        if (Objects.isNull(dvsParentParam.getParentId())) {
            dvsParent.setCreateTime(LocalDateTime.now());
        }
        dvsParent.setDvsCode(dvsParentParam.getDvsCode());
        dvsParent.setUpdateTime(LocalDateTime.now());
        dvsParent.setIsDeleted(IsDeletedEnum.NO.getValue());
        dvsParent.setTenantId(userInfo.getTenantId());
        dvsParent.setTenantName(userInfo.getTenantName());
        dvsParent.setDeptId(userInfo.getDeptId());
        dvsParent.setDeptName(userInfo.getDeptName());
        dvsParent.setUserId(userInfo.getUserId());
        dvsParent.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(dvsParent)) {
            throw new DtvsAdminException("保存数据源失败");
        }

        return dvsParent.getParentId();
    }

    private void validateAddDvsParent(DvsParentParam dvsParentParam) throws DtvsAdminException {
        if (Objects.isNull(dvsParentParam)) {
            throw new DtvsAdminException("数据空间父类参数为空");
        }
        if (StringUtils.isBlank(dvsParentParam.getParentName())) {
            throw new DtvsAdminException("数据空间父类名称为空");
        }
        if (dvsParentParam.getParentName().length() > 50) {
            throw new DtvsAdminException("数据空间父类名称超过最大长度");
        }
        if (!dvsParentParam.getParentName().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据空间父类名称只能为英文开头,包含数字下划线");
        }
        if (StringUtils.isBlank(dvsParentParam.getParentAlias())) {
            throw new DtvsAdminException("数据空间父类别名为空");
        }
        if (dvsParentParam.getParentAlias().length() > 50) {
            throw new DtvsAdminException("数据空间父类别名超过最大长度");
        }
        if (StringUtils.isBlank(dvsParentParam.getParentAbbr())) {
            throw new DtvsAdminException("数据空间父类简称为空");
        }
        if (dvsParentParam.getParentAbbr().length() > 20) {
            throw new DtvsAdminException("数据空间父类简称超过最大长度");
        }
        if (!dvsParentParam.getParentAbbr().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据空间父类简称只能为为英文开头,包含数字下划线");
        }
        if (Objects.isNull(dvsParentParam.getEnvMode())) {
            throw new DtvsAdminException("数据空间父类模式为空");
        }
        if (!EnvModelEnum.existsValue(dvsParentParam.getEnvMode())) {
            throw new DtvsAdminException("数据空间父类模式不存在");
        }
        if (CollectionUtil.isEmpty(dvsParentParam.getDvsParamList())) {
            throw new DtvsAdminException("数据空间参数为空");
        }
        for (DvsParam dvsParam : dvsParentParam.getDvsParamList()) {
            if (StringUtils.isBlank(dvsParam.getDvsName())) {
                throw new DtvsAdminException("数据空间名称为空");
            }
            if (StringUtils.isBlank(dvsParam.getDvsAlias())) {
                throw new DtvsAdminException("数据空间别名为空");
            }
            if (StringUtils.isBlank(dvsParam.getDvsAbbr())) {
                throw new DtvsAdminException("数据空间简称为空");
            }
            if (Objects.isNull(dvsParam.getEnv())) {
                throw new DtvsAdminException("数据空间环境为空");
            }
        }
        // 检查数据存储桶
        if (CollectionUtil.isNotEmpty(dvsParentParam.getStorageBoxParamList())) {
            List<StorageBoxParam> storageBoxParamList = dvsParentParam.getStorageBoxParamList();
            List<Long> storageIds = storageBoxParamList.stream().map(StorageBoxParam::getStorageId).distinct().collect(Collectors.toList());
            ListStorageParam listStorageParam = new ListStorageParam();
            listStorageParam.setStorageIds(storageIds);
            List<StorageFeignVO> storageFeignVOList = manageFeignClient.listStorageVO(listStorageParam);
            if (CollectionUtil.isEmpty(storageFeignVOList)) {
                throw new DtvsAdminException("存储区不存在");
            }
            if (dvsParentParam.getMultStorage().intValue() == MulStorageEnum.SIGNAL_STORAGE.getValue()) {
                if (storageFeignVOList.size() != 1) {
                    throw new DtvsAdminException("单存储区选择的存储区只能为一个");
                }
                if (dvsParentParam.getStorageBoxParamList().size() !=1) {
                    throw new DtvsAdminException("单存储区选择的存储区只能为一个");
                }
            }
            if (dvsParentParam.getMultStorage().intValue() == MulStorageEnum.MULTI_STORAGE.getValue()) {
                if (storageFeignVOList.size() != 2) {
                    throw new DtvsAdminException("多存储区选择的存储区只能为两个");
                }
                if (dvsParentParam.getStorageBoxParamList().size() !=2) {
                    throw new DtvsAdminException("多存储区选择的存储区只能为两个");
                }
            }
            for (StorageBoxParam storageBoxParam : storageBoxParamList) {
                StorageFeignVO storageFeignVO = storageFeignVOList.stream().filter(s -> s.getStorageId().longValue() == storageBoxParam.getStorageId().longValue()).findFirst().orElse(null);
                if (Objects.isNull(storageFeignVO)) {
                    throw new DtvsAdminException("存储区不存在");
                }
                if (!storageFeignVO.getStorageName().equals(storageBoxParam.getStorageName())) {
                    throw new DtvsAdminException("存储桶配置存储名称和存储区不一致");
                }
                if (!storageFeignVO.getStorageType().equals(storageBoxParam.getStorageType())) {
                    throw new DtvsAdminException("存储桶配置存储类型和存储区不一致");
                }
//                if (Objects.nonNull(storageBoxParam.getStorageFormat()) && !StorageFormatEnum.existsValue(storageBoxParam.getStorageFormat())) {
//                    throw new DtvsAdminException("存储桶配置存储格式不存在");
//                }
                if (storageBoxParam.getStorageType().toUpperCase().equals(StorageTypeEnum.HDFS.getDesc()) && StringUtils.isBlank(storageBoxParam.getStoragePath())) {
                    throw new DtvsAdminException("HDFS模式下存储路径不能为空");
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editDvsParent(DvsParentParam dvsParentParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        DvsParent dvsParent = validateEditDvsParent(dvsParentParam, userInfo);
        // 编辑数据空间父类
        Long parentId = saveOrUpdateDvsParent(dvsParentParam, userInfo);
        // 新增数据空间
        dvsParentParam.setDvsCode(dvsParent.getDvsCode());
        saveDvs(dvsParentParam, userInfo);
        // 新增数据存储桶
        saveStorageBox(dvsParentParam, userInfo);
        return parentId;
    }

    private DvsParent validateEditDvsParent(DvsParentParam DvsParentParam, UserInfo userInfo) throws DtvsAdminException {
        DvsParent dvsParent;
        validateAddDvsParent(DvsParentParam);
        if (Objects.isNull(DvsParentParam.getParentId())) {
            throw new DtvsAdminException("数据空间父类ID不能为空");
        }
        if (Objects.isNull(dvsParent = getOne(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentId, DvsParentParam.getParentId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("数据空间父类不存在");
        }
        List<DvsParent> DvsParents = list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentName, DvsParentParam.getParentName()).
                eq(DvsParent::getTenantId, userInfo.getTenantId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(DvsParents)) {
            if (DvsParents.stream().filter(d -> !d.getParentId().equals(dvsParent.getParentId())).count() > 0) {
                throw new DtvsAdminException("数据空间父类名称重复");
            }
        }

        List<DvsParent> DvsParents2 = list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentAlias, DvsParentParam.getParentName()).
                eq(DvsParent::getTenantId, userInfo.getTenantId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(DvsParents2)) {
            if (DvsParents2.stream().filter(d -> !d.getParentId().equals(dvsParent.getParentId())).count() > 0) {
                throw new DtvsAdminException("数据空间父类别名重复");
            }
        }

        List<DvsParent> DvsParents3 = list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentAbbr, DvsParentParam.getParentName()).
                eq(DvsParent::getTenantId, userInfo.getTenantId()).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(DvsParents3)) {
            if (DvsParents3.stream().filter(d -> !d.getParentId().equals(dvsParent.getParentId())).count() > 0) {
                throw new DtvsAdminException("数据空间父类简称重复");
            }
        }

        // todo 是否判断在表存储中已经使用了项目空间 如果使用是否不允许编辑

        // 删除旧的数据空间
        if (CollectionUtil.isNotEmpty(dvsService.list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsCode, dvsParent.getDvsCode()).
                eq(Dvs::getTenantId, userInfo.getTenantId()).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()))) && !dvsService.remove(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsCode, dvsParent.getDvsCode()).
                eq(Dvs::getTenantId, userInfo.getTenantId()).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()))) {
            throw new DtvsAdminException("删除旧的数据空间失败");
        }
        if (CollectionUtil.isNotEmpty(storageBoxService.list(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getDvsCode, dvsParent.getDvsCode()))) && !storageBoxService.remove(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getDvsCode, dvsParent.getDvsCode()))) {
            throw new DtvsAdminException("删除数据空间对应的存储区失败");
        }
        return dvsParent;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean addStorageBox(List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException {
        Boolean flag = Boolean.TRUE;
        validateAddStorageBox(storageBoxParamList);
        String storageBoxCode = UCodeUtil.produce();
        storageBoxParamList.forEach(s -> {
            s.setStorageBoxCode(storageBoxCode);
        });
        flag = saveOrUpdateStorageBox(storageBoxParamList);
        return flag;
    }

    private Boolean saveOrUpdateStorageBox(List<StorageBoxParam> storageBoxParamList) {
        List<StorageBox> storageBoxList = mapperFactory.getMapperFacade().mapAsList(storageBoxParamList, StorageBox.class);
        if (CollectionUtil.isNotEmpty(storageBoxList)) {
            storageBoxList.forEach(storageBox -> {
                if (Objects.isNull(storageBox.getBoxId())) {
                    storageBox.setCreateTime(LocalDateTime.now());
                    storageBox.setTableCount(0);
                    if (Objects.isNull(storageBox.getStorageFormat())) {
                        storageBox.setStorageFormat(StorageFormatEnum.ORC.getValue());
                    }
                }
                storageBox.setUpdateTime(LocalDateTime.now());
            });
            return storageBoxService.saveOrUpdateBatch(storageBoxList);
        }
        return Boolean.FALSE;
    }

    private void validateAddStorageBox(List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException {
        if (CollectionUtil.isEmpty(storageBoxParamList)) {
            throw new DtvsAdminException("数据空间存储区参数为空");
        }
        for (StorageBoxParam storageBoxParam : storageBoxParamList) {
            if (StringUtils.isBlank(storageBoxParam.getDvsCode())) {
                throw new DtvsAdminException("数据空间存储区数据空间编码为空");
            }
            if (Objects.isNull(storageBoxParam.getStorageId())) {
                throw new DtvsAdminException("数据空间存储区数据存储ID为空");
            }
            if (StringUtils.isBlank(storageBoxParam.getBoxName())) {
                throw new DtvsAdminException("数据空间存储区名称为空");
            }
            if (Objects.isNull(storageBoxParam.getDwLayer())) {
                throw new DtvsAdminException("数据空间存储区分层参数为空");
            }
            if (StringUtils.isBlank(storageBoxParam.getStorageName())) {
                throw new DtvsAdminException("数据空间存储区数据存储名称为空");
            }
            if (Objects.isNull(storageBoxParam.getStorageType())) {
                throw new DtvsAdminException("数据空间存储区数据存储类型为空");
            }
            if (Objects.isNull(storageBoxParam.getEnv())) {
                throw new DtvsAdminException("数据空间存储区环境为空");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean editStorageBox(List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException {
        Boolean flag = Boolean.TRUE;
        validateEditStorageBox(storageBoxParamList);
        flag = saveOrUpdateStorageBox(storageBoxParamList);
        return flag;
    }

    private void validateEditStorageBox(List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException {
        validateAddStorageBox(storageBoxParamList);
        for (StorageBoxParam storageBoxParam : storageBoxParamList) {
            if (Objects.isNull(storageBoxParam.getStorageBoxId())) {
                throw new DtvsAdminException("数据空间存储区ID为空");
            }
        }
    }

    @Override
    public DvsParentVO detail(Long parentId) throws DtvsAdminException {
        DvsParent dvsParent;
        if (Objects.isNull(parentId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(dvsParent = getOne(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentId, parentId).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("数据空间不存在");
        }
        DvsParentVO dvsParentVO = mapperFactory.getMapperFacade().map(dvsParent, DvsParentVO.class);
        String dvsCode = dvsParentVO.getDvsCode();
        if (StringUtils.isNoneBlank(dvsCode)) {
            List<DvsVO> dvsList = mapperFactory.getMapperFacade().mapAsList(dvsService.list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsCode, dvsCode).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue())), DvsVO.class);
            List<DvsMemberVO> dvsMemberList = mapperFactory.getMapperFacade().mapAsList(dvsMemberService.list(Wrappers.<DvsMember>lambdaQuery().eq(DvsMember::getDvsCode, dvsCode)), DvsMemberVO.class);
            List<EtlJob> etlJobList = etlJobService.list(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getDvsCode, dvsCode).eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
            List<BdmJob> bdmJobList = bdmJobService.list(Wrappers.<BdmJob>lambdaQuery().eq(BdmJob::getDvsCode, dvsCode).eq(BdmJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
            List<DvsTable> dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().eq(DvsTable::getDvsCode, dvsCode));
            List<StorageBoxVO> storageBoxVOList = mapperFactory.getMapperFacade().mapAsList(storageBoxService.list(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getDvsCode, dvsCode)), StorageBoxVO.class);

            if (CollectionUtil.isNotEmpty(storageBoxVOList)) {
                dvsParentVO.setStorageBoxVOList(storageBoxVOList);
            }

            if (CollectionUtil.isNotEmpty(dvsList)) {
                dvsParentVO.setDvsVOList(dvsList);
            }
            if (CollectionUtil.isNotEmpty(dvsMemberList)) {
                dvsParentVO.setDvsMemberVOList(dvsMemberList);
            }
            DvsDetailCountVO dvsDetailCountVO = new DvsDetailCountVO();
            if (CollectionUtil.isNotEmpty(etlJobList)) {
                Map<Integer, List<EtlJob>> etlJobMap = etlJobList.stream().collect(Collectors.groupingBy(EtlJob::getEnv));
                if (CollectionUtil.isNotEmpty(etlJobMap)) {
                    List<EtlJob> etlJobs;
                    if (CollectionUtil.isNotEmpty(etlJobs = etlJobMap.get(EnvEnum.BAISC.getValue()))) {
                        int etlCount = etlJobs.size();
                        int etlFailCount = (int) etlJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue())).count();
                        dvsDetailCountVO.setBasicEtlJobCount(etlCount);
                        dvsDetailCountVO.setBasicEtlJobFailCount(etlFailCount);
                    }
                    if (CollectionUtil.isNotEmpty(etlJobs = etlJobMap.get(EnvEnum.DEV.getValue()))) {
                        int etlCount = etlJobs.size();
                        int etlFailCount = (int) etlJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue())).count();
                        dvsDetailCountVO.setDevEtlJobCount(etlCount);
                        dvsDetailCountVO.setDevEtlJobFailCount(etlFailCount);
                    }
                    if (CollectionUtil.isNotEmpty(etlJobs = etlJobMap.get(EnvEnum.PROD.getValue()))) {
                        int etlCount = etlJobs.size();
                        int etlFailCount = (int) etlJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue())).count();
                        dvsDetailCountVO.setProdEtlJobCount(etlCount);
                        dvsDetailCountVO.setProdEtlJobFailCount(etlFailCount);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(bdmJobList)) {
                Map<Integer, List<BdmJob>> bdmJobMap = bdmJobList.stream().collect(Collectors.groupingBy(BdmJob::getEnv));
                if (CollectionUtil.isNotEmpty(bdmJobMap)) {
                    List<BdmJob> bdmJobs;
                    if (CollectionUtil.isNotEmpty(bdmJobs = bdmJobMap.get(EnvEnum.BAISC.getValue()))) {
                        int bdmCount = bdmJobs.size();
                        int bdmFailCount = (int) bdmJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue())).count();
                        dvsDetailCountVO.setBasicBdmJobCount(bdmCount);
                        dvsDetailCountVO.setBasicBdmJobFailCount(bdmFailCount);
                    }
                    if (CollectionUtil.isNotEmpty(bdmJobs = bdmJobMap.get(EnvEnum.DEV.getValue()))) {
                        int bdmCount = bdmJobs.size();
                        int bdmFailCount = (int) bdmJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue())).count();
                        dvsDetailCountVO.setDevBdmJobCount(bdmCount);
                        dvsDetailCountVO.setDevBdmJobFailCount(bdmFailCount);
                    }
                    if (CollectionUtil.isNotEmpty(bdmJobs = bdmJobMap.get(EnvEnum.PROD.getValue()))) {
                        int bdmCount = bdmJobs.size();
                        int bdmFailCount = (int) bdmJobs.stream().filter(e -> e.getJobLifecycle().equals(JobLifecycleEnum.FAIL.getValue())).count();
                        dvsDetailCountVO.setProdBdmJobCount(bdmCount);
                        dvsDetailCountVO.setProdBdmJobFailCount(bdmFailCount);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(dvsTableList)) {
                Map<Integer, List<DvsTable>> dvsTableMap = dvsTableList.stream().collect(Collectors.groupingBy(DvsTable::getEnv));
                if (CollectionUtil.isNotEmpty(dvsTableMap)) {
                    List<DvsTable> dvsTables;
                    if (CollectionUtil.isNotEmpty(dvsTables = dvsTableMap.get(EnvEnum.BAISC.getValue()))) {
                        int tableCount = dvsTables.size();
                        dvsDetailCountVO.setBasicTableCount(tableCount);
                    }
                    if (CollectionUtil.isNotEmpty(dvsTables = dvsTableMap.get(EnvEnum.DEV.getValue()))) {
                        int tableCount = dvsTables.size();
                        dvsDetailCountVO.setDevTableCount(tableCount);
                    }
                    if (CollectionUtil.isNotEmpty(dvsTables = dvsTableMap.get(EnvEnum.PROD.getValue()))) {
                        int tableCount = dvsTables.size();
                        dvsDetailCountVO.setProdTableCount(tableCount);
                    }
                }
            }

            dvsParentVO.setDvsDetailCountVO(dvsDetailCountVO);
        }


        return dvsParentVO;
    }

    @Override
    public Boolean delete(Long parentId) throws DtvsAdminException {
        DvsParent dvsParent;
        if (Objects.isNull(parentId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(dvsParent = getOne(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getParentId, parentId).eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("数据空间不存在");
        }
        DvsParent updateDvsParent = new DvsParent();
        updateDvsParent.setParentId(dvsParent.getParentId());
        updateDvsParent.setIsDeleted(IsDeletedEnum.NO.getValue());
        if (!saveOrUpdate(updateDvsParent)) {
            throw new DtvsAdminException("删除数据空间父类失败");
        }
        List<Dvs> dvsList = dvsService.list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsCode, dvsParent.getDvsCode()).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(dvsList)) {
            List<Dvs> updateDvsList = new ArrayList<>();
            dvsList.forEach(d -> {
                Dvs updateDvs = new Dvs();
                updateDvs.setDvsId(d.getDvsId());
                updateDvs.setIsDeleted(IsDeletedEnum.NO.getValue());
                updateDvs.setUpdateTime(LocalDateTime.now());
                updateDvsList.add(updateDvs);
            });
            if (!dvsService.saveOrUpdateBatch(updateDvsList)) {
                throw new DtvsAdminException("删除数据空间失败");
            }
        }
        return Boolean.TRUE;
    }
}
