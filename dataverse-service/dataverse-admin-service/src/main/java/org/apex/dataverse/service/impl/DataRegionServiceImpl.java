package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.DataRegion;
import org.apex.dataverse.entity.Dvs;
import org.apex.dataverse.enums.*;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.DataRegionMapper;
import org.apex.dataverse.mapper.DvsMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DataRegionParam;
import org.apex.dataverse.param.ListDataRegionParam;
import org.apex.dataverse.param.PageDataRegionParam;
import org.apex.dataverse.service.IDataRegionService;
import org.apex.dataverse.service.IDvsService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.UCodeUtil;
import org.apex.dataverse.vo.DataRegionVO;
import org.apex.dataverse.vo.DwLayerVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据域 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Service
public class DataRegionServiceImpl extends ServiceImpl<DataRegionMapper, DataRegion> implements IDataRegionService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private DvsMapper dvsMapper;

    @Override
    public PageResult<DataRegionVO> pageDataRegion(PageDataRegionParam pageDataRegionParam) throws DtvsAdminException {
        validatePageDataRegion(pageDataRegionParam);
        IPage<DataRegion> iPage = getIPage(pageDataRegionParam);
        PageResult<DataRegionVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DataRegionVO.class));
        buildPageDataRegion(pageResult.getList());
        return pageResult;
    }

    private void buildPageDataRegion(List<DataRegionVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> dvsCodeList = list.stream().map(DataRegionVO::getDvsCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(dvsCodeList)) {
                List<Dvs> dvsList = dvsMapper.selectList(Wrappers.<Dvs>lambdaQuery().in(Dvs::getDvsCode, dvsCodeList).eq(Dvs::getEnv, list.get(0).getEnv()).
                        eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()));
                if (CollectionUtil.isNotEmpty(dvsList)) {
                    list.forEach(l -> {
                        if (l.getEnv().intValue() == EnvEnum.DEV.getValue()) {
                            l.setEnvMode(EnvModelEnum.DEV_PROD.getValue());
                        } else {
                            l.setEnvMode(EnvModelEnum.BAISC.getValue());
                        }
                        Dvs dvs = dvsList.stream().filter(d -> d.getDvsCode().equals(l.getDvsCode())).findFirst().orElse(null);
                        if (Objects.nonNull(dvs)) {
                            l.setDvsName(dvs.getDvsAlias());
                        }
                    });
                }
            }
        }
    }

    private IPage<DataRegion> getIPage(PageDataRegionParam pageDataRegionParam) {
        //默认单页10条记录
        Page<DataRegion> page = new Page<>();
        PageQueryParam pageQueryParam = pageDataRegionParam.getPageQueryParam();
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

        LambdaQueryWrapper<DataRegion> queryWrapper = Wrappers.lambdaQuery();

        if (StringUtils.isNotEmpty(pageDataRegionParam.getDvsCode())) {
            queryWrapper.eq(DataRegion::getDvsCode, pageDataRegionParam.getDvsCode());
        }

        if (Objects.nonNull(pageDataRegionParam.getDwLayerDetail())) {
            queryWrapper.eq(DataRegion::getDwLayerDetail, pageDataRegionParam.getDwLayerDetail());
        }

        if(Objects.nonNull(pageDataRegionParam.getJobType())){
            if (pageDataRegionParam.getJobType().intValue() == JobTypeEnum.ETL_JOB.getValue()) {
                queryWrapper.eq(DataRegion::getDwLayer, DwLayerEnum.ODS.getValue());
            }
            if (pageDataRegionParam.getJobType().intValue() == JobTypeEnum.BDM_JOB.getValue()) {
                queryWrapper.ne(DataRegion::getDwLayer, DwLayerEnum.ODS.getValue());
            }
        }

        if (StringUtils.isNotEmpty(pageDataRegionParam.getKeyword())) {
            if (pageDataRegionParam.getKeyword().matches("[0-9]+$")) {
                queryWrapper.eq(DataRegion::getDataRegionId, Long.parseLong(pageDataRegionParam.getKeyword()));
            } else {
                queryWrapper.nested(w -> w.like(DataRegion::getRegionName, pageDataRegionParam.getKeyword())
                        .or(o -> o.like(DataRegion::getRegionAlias, pageDataRegionParam.getKeyword()))
                        .or(s -> s.like(DataRegion::getRegionAbbr, pageDataRegionParam.getKeyword())));
            }
        }
        queryWrapper.eq(DataRegion::getIsDeleted, IsDeletedEnum.NO.getValue());
        // 过滤prod环境模式下的数据域
        queryWrapper.ne(DataRegion::getEnv, EnvEnum.PROD.getValue());
        return page(page, queryWrapper);
    }

    private void validatePageDataRegion(PageDataRegionParam pageDataRegionParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageDataRegionParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageDataRegionParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean addDataRegion(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        Boolean flag = Boolean.TRUE;
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateAddDataRegion(dataRegionParamList);
        validateDataRegionDuplicate(dataRegionParamList);
        String dataRegionCode = UCodeUtil.produce();
        dataRegionParamList.forEach(d -> {
            d.setDataRegionCode(dataRegionCode);
        });
        flag = saveOrUpdateBatchDataRegion(dataRegionParamList, userInfo);
        return flag;
    }

    private Boolean saveOrUpdateBatchDataRegion(List<DataRegionParam> dataRegionParamList, UserInfo userInfo) {
        List<DataRegion> dataRegionList = mapperFactory.getMapperFacade().mapAsList(dataRegionParamList, DataRegion.class);
        if (CollectionUtil.isNotEmpty(dataRegionList)) {
            dataRegionList.forEach(d -> {
                d.setCreateTime(LocalDateTime.now());
                d.setUpdateTime(LocalDateTime.now());
                d.setIsDeleted(IsDeletedEnum.NO.getValue());
                d.setUserId(userInfo.getUserId());
                d.setUserName(userInfo.getUserName());

            });
            return saveOrUpdateBatch(dataRegionList);
        }
        return Boolean.FALSE;
    }

    private void validateDataRegionDuplicate(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        if (CollectionUtil.isNotEmpty(dataRegionParamList)) {
            DataRegionParam dataRegionParam = dataRegionParamList.get(0);
            // 检查数据域名称
            List<DataRegion> dataRegions = list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getRegionName, dataRegionParam.getRegionName()).
                    eq(DataRegion::getDvsCode, dataRegionParam.getDvsCode()));
            if (CollectionUtil.isNotEmpty(dataRegions)) {
                throw new DtvsAdminException("数据域名称重复");
            }
            // 检查数据域别名
            List<DataRegion> dataRegions2 = list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getRegionName, dataRegionParam.getRegionAlias()).
                    eq(DataRegion::getDvsCode, dataRegionParam.getDvsCode()));
            if (CollectionUtil.isNotEmpty(dataRegions2)) {
                throw new DtvsAdminException("数据域别名重复");
            }
            // 检查数据域简称
            List<DataRegion> dataRegions3 = list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getRegionName, dataRegionParam.getRegionAbbr()).
                    eq(DataRegion::getDvsCode, dataRegionParam.getDvsCode()));
            if (CollectionUtil.isNotEmpty(dataRegions3)) {
                throw new DtvsAdminException("数据域简称重复");
            }
        }
    }

    private void validateAddDataRegion(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        if (CollectionUtil.isNotEmpty(dataRegionParamList)) {
            for (DataRegionParam dataRegionParam : dataRegionParamList) {
                if (StringUtils.isBlank(dataRegionParam.getRegionName())) {
                    throw new DtvsAdminException("数据域名称为空");
                }
                if (StringUtils.isBlank(dataRegionParam.getRegionAlias())) {
                    throw new DtvsAdminException("数据域别名为空");
                }
                if (StringUtils.isBlank(dataRegionParam.getRegionAbbr())) {
                    throw new DtvsAdminException("数据域简称为空");
                }
                if (StringUtils.isBlank(dataRegionParam.getDvsCode())) {
                    throw new DtvsAdminException("数据空间编码为空");
                }
                if (Objects.isNull(dataRegionParam.getEnv())) {
                    throw new DtvsAdminException("数据空间环境为空");
                }
                if (Objects.isNull(dataRegionParam.getDwLayer())) {
                    throw new DtvsAdminException("数据域分层为空");
                }
                if (Objects.isNull(dataRegionParam.getDwLayerDetail())) {
                    throw new DtvsAdminException("数据域分层明细为空");
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean editDataRegion(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        Boolean flag = Boolean.TRUE;
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        DataRegion dataRegion = validateEditDataRegion(dataRegionParamList);
        validateEditDataRegionDuplicate(dataRegionParamList.get(0));
        if (!remove(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getDataRegionCode, dataRegion.getDataRegionCode()).eq(DataRegion::getDvsCode, dataRegion.getDvsCode()))) {
            throw new DtvsAdminException("删除旧的数据域失败");
        }
        String dataRegionCode = UCodeUtil.produce();
        dataRegionParamList.forEach(d -> {
            d.setDataRegionCode(dataRegionCode);
        });
        flag = saveOrUpdateBatchDataRegion(dataRegionParamList, userInfo);
        return flag;
    }

    private Boolean saveOrUpdateDataRegion(DataRegionParam dataRegionParam, UserInfo userInfo) {
        Boolean flag = Boolean.FALSE;
        DataRegion dataRegion = mapperFactory.getMapperFacade().map(dataRegionParam, DataRegion.class);
        dataRegion.setUpdateTime(LocalDateTime.now());
        dataRegion.setUserId(userInfo.getUserId());
        dataRegion.setUserName(userInfo.getUserName());
        flag = saveOrUpdate(dataRegion);
        return flag;
    }

    private void validateEditDataRegionDuplicate(DataRegionParam dataRegionParam) throws DtvsAdminException {
        List<DataRegion> dataRegions = list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getRegionName, dataRegionParam.getRegionName()).
                eq(DataRegion::getDvsCode, dataRegionParam.getDvsCode()));
        if (CollectionUtil.isNotEmpty(dataRegions)) {
            if (dataRegions.stream().filter(d -> !d.getDataRegionId().equals(dataRegionParam.getDataRegionId())).count() > 0) {
                throw new DtvsAdminException("数据域名称重复");
            }
        }

        List<DataRegion> dataRegions2 = list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getRegionName, dataRegionParam.getRegionAlias()).
                eq(DataRegion::getDvsCode, dataRegionParam.getDvsCode()));
        if (CollectionUtil.isNotEmpty(dataRegions2)) {
            if (dataRegions2.stream().filter(d -> !d.getDataRegionId().equals(dataRegionParam.getDataRegionId())).count() > 0) {
                throw new DtvsAdminException("数据域名称重复");
            }
        }

        List<DataRegion> dataRegions3 = list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getRegionName, dataRegionParam.getRegionAbbr()).
                eq(DataRegion::getDvsCode, dataRegionParam.getDvsCode()));
        if (CollectionUtil.isNotEmpty(dataRegions3)) {
            if (dataRegions3.stream().filter(d -> !d.getDataRegionId().equals(dataRegionParam.getDataRegionId())).count() > 0) {
                throw new DtvsAdminException("数据域名称重复");
            }
        }
    }

    private DataRegion validateEditDataRegion(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        if (CollectionUtil.isEmpty(dataRegionParamList)) {
            throw new DtvsAdminException("数据域参数为空");
        }
        for (DataRegionParam dataRegionParam : dataRegionParamList) {
            if (Objects.isNull(dataRegionParam.getDataRegionId())) {
                throw new DtvsAdminException("数据域ID参数为空");
            }
            if (StringUtils.isBlank(dataRegionParam.getRegionName())) {
                throw new DtvsAdminException("数据域名称为空");
            }
            if (StringUtils.isBlank(dataRegionParam.getRegionAlias())) {
                throw new DtvsAdminException("数据域别名为空");
            }
            if (StringUtils.isBlank(dataRegionParam.getRegionAbbr())) {
                throw new DtvsAdminException("数据域简称为空");
            }
            if (StringUtils.isBlank(dataRegionParam.getDvsCode())) {
                throw new DtvsAdminException("数据空间编码为空");
            }
            if (Objects.isNull(dataRegionParam.getEnv())) {
                throw new DtvsAdminException("数据空间环境为空");
            }
            if (Objects.isNull(dataRegionParam.getDwLayer())) {
                throw new DtvsAdminException("数据域分层为空");
            }
            if (Objects.isNull(dataRegionParam.getDwLayerDetail())) {
                throw new DtvsAdminException("数据域分层明细为空");
            }
        }
        List<Long> dataRegionIds = dataRegionParamList.stream().map(DataRegionParam::getDataRegionId).collect(Collectors.toList());
        List<DataRegion> dataRegions = list(Wrappers.<DataRegion>lambdaQuery().in(DataRegion::getDataRegionId, dataRegionIds).eq(DataRegion::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isEmpty(dataRegions)) {
            throw new DtvsAdminException("数据域不存在");
        }

        return dataRegions.get(0);
    }

    @Override
    public List<DwLayerVO> dwLayer() {
        List<DwLayerVO> layerVOList = new ArrayList<>();
        for (DwLayerEnum dwLayerEnum : DwLayerEnum.values()) {
            DwLayerVO dwLayerVO = new DwLayerVO();
            dwLayerVO.setValue(dwLayerEnum.getValue());
            dwLayerVO.setDesc(dwLayerEnum.getDesc());
            layerVOList.add(dwLayerVO);
        }
        return layerVOList;
    }

    @Override
    public List<DwLayerVO> dwLayerDetail() {
        List<DwLayerVO> layerVOList = new ArrayList<>();
        for (DwLayerDetailEnum dwLayerDetailEnum : DwLayerDetailEnum.values()) {
            DwLayerVO dwLayerVO = new DwLayerVO();
            dwLayerVO.setValue(dwLayerDetailEnum.getValue());
            dwLayerVO.setDesc(dwLayerDetailEnum.getDesc());
            layerVOList.add(dwLayerVO);
        }
        return layerVOList;
    }

    @Override
    public DataRegionVO detail(Long dataRegionId) throws DtvsAdminException {
        DataRegion dataRegion = validateDataRegionExists(dataRegionId);
        DataRegionVO dataRegionVO = mapperFactory.getMapperFacade().map(dataRegion, DataRegionVO.class);
        Dvs dvs = dvsMapper.selectOne(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsCode, dataRegion.getDvsCode()).eq(Dvs::getEnv, dataRegion.getEnv()).
                eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(dvs)) {
            throw new DtvsAdminException("数据空间不存在");
        }
        dataRegionVO.setDvsName(dvs.getDvsName());
        if (dataRegionVO.getEnv().intValue() != EnvEnum.BAISC.getValue()) {
            dataRegionVO.setEnvMode(EnvModelEnum.DEV_PROD.getValue());
        } else {
            dataRegionVO.setEnvMode(EnvModelEnum.BAISC.getValue());
        }
        return dataRegionVO;
    }

    private DataRegion validateDataRegionExists(Long dataRegionId) throws DtvsAdminException {
        DataRegion dataRegion;
        if (Objects.isNull(dataRegionId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(dataRegion = getOne(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getDataRegionId, dataRegionId).eq(DataRegion::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("数据域不存在");
        }

        return dataRegion;
    }

    @Override
    public Boolean delete(Long dataRegionId) throws DtvsAdminException {
        validateDataRegionExists(dataRegionId);
        DataRegion updateDataRegion = new DataRegion();
        updateDataRegion.setDataRegionId(dataRegionId);
        updateDataRegion.setIsDeleted(IsDeletedEnum.YES.getValue());
        updateDataRegion.setUpdateTime(LocalDateTime.now());
        if (!saveOrUpdate(updateDataRegion)) {
            throw new DtvsAdminException("删除数据域失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public List<DataRegionVO> listDataRegionByDvsAndEnv(ListDataRegionParam listDataRegionParam) throws DtvsAdminException {
        validateListDataRegionByDvsAndEnv(listDataRegionParam);
        LambdaQueryWrapper<DataRegion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataRegion::getDvsCode, listDataRegionParam.getDvsCode());
        queryWrapper.eq(DataRegion::getEnv, listDataRegionParam.getEnv());
        queryWrapper.eq(DataRegion::getIsDeleted, IsDeletedEnum.NO.getValue());
        if (Objects.nonNull(listDataRegionParam.getDwLayer())) {
            queryWrapper.eq(DataRegion::getDwLayer, listDataRegionParam.getDwLayer());
        }
        if (StringUtils.isNotBlank(listDataRegionParam.getDwLayerDetail())) {
            queryWrapper.eq(DataRegion::getDwLayerDetail, listDataRegionParam.getDwLayerDetail());
        }
        List<DataRegion> dataRegionList = list(queryWrapper);
        List<DataRegionVO> dataRegionVOList = mapperFactory.getMapperFacade().mapAsList(dataRegionList, DataRegionVO.class);
        if (CollectionUtil.isNotEmpty(dataRegionVOList)) {
            dataRegionVOList.forEach(d -> {
                if (d.getEnv().intValue() != EnvEnum.BAISC.getValue()) {
                    d.setEnvMode(EnvModelEnum.DEV_PROD.getValue());
                } else {
                    d.setEnvMode(EnvModelEnum.BAISC.getValue());
                }
            });
        }
        return dataRegionVOList;
    }

    private void validateListDataRegionByDvsAndEnv(ListDataRegionParam listDataRegionParam) throws DtvsAdminException {
        if (Objects.isNull(listDataRegionParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(listDataRegionParam.getDvsCode())) {
            throw new DtvsAdminException("数据空间编码不能为空");
        }
        if (Objects.isNull(listDataRegionParam.getEnv())) {
            throw new DtvsAdminException("数据空间环境不能为空");
        }
    }

    @Override
    public List<DwLayerVO> getDwLayerDetail(String layer) {
        List<DwLayerVO> layerVOList = new ArrayList<>();
        for (DwLayerDetailEnum dwLayerDetailEnum : DwLayerDetailEnum.values()) {
            if (layer.equals(dwLayerDetailEnum.getLayer())) {
                DwLayerVO dwLayerVO = new DwLayerVO();
                dwLayerVO.setValue(dwLayerDetailEnum.getValue());
                dwLayerVO.setDesc(dwLayerDetailEnum.getDesc());
                layerVOList.add(dwLayerVO);
            }
        }
        return layerVOList;
    }
}
