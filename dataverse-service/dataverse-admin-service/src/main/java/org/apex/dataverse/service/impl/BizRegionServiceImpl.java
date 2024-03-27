package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.BizRegion;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.BizRegionMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.BizRegionParam;
import org.apex.dataverse.param.PageBizRegionParam;
import org.apex.dataverse.service.IBizRegionService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.vo.BizRegionVO;
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
public class BizRegionServiceImpl extends ServiceImpl<BizRegionMapper, BizRegion> implements IBizRegionService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public PageResult<BizRegionVO> pageBizRegion(PageBizRegionParam pageBizRegionParam) throws DtvsAdminException {
        validateBizRegion(pageBizRegionParam);
        IPage<BizRegion> iPage = getIPage(pageBizRegionParam);
        PageResult<BizRegionVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), BizRegionVO.class));
        return pageResult;
    }

    private IPage<BizRegion> getIPage(PageBizRegionParam pageBizRegionParam) throws DtvsAdminException {
        // 默认单页10条记录
        Page<BizRegion> page = new Page<>();
        PageQueryParam pageQueryParam = pageBizRegionParam.getPageQueryParam();
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

        LambdaQueryWrapper<BizRegion> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(pageBizRegionParam.getBizRegionName())) {
            queryWrapper.eq(BizRegion::getBizRegionName, pageBizRegionParam.getBizRegionName());
        }
        queryWrapper.eq(BizRegion::getIsDeleted, IsDeletedEnum.NO.getValue());
        return page(page, queryWrapper);
    }

    private void validateBizRegion(PageBizRegionParam pageBizRegionParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageBizRegionParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageBizRegionParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException {
        validateAddBizRegion(bizRegionParam);
        valiadateAddBizRegionDuplicate(bizRegionParam);
        Long bizRegionId = saveOrUpdateBizRegion(bizRegionParam);
        return bizRegionId;
    }

    private Long saveOrUpdateBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException {
        BizRegion bizRegion = mapperFactory.getMapperFacade().map(bizRegionParam, BizRegion.class);
        if (Objects.isNull(bizRegionParam.getBizRegionId())) {
            bizRegion.setCreateTime(LocalDateTime.now());
        }
        bizRegion.setUpdateTime(LocalDateTime.now());
        bizRegion.setIsDeleted(IsDeletedEnum.NO.getValue());
        if (!saveOrUpdate(bizRegion)) {
            throw new DtvsAdminException("保存业务域失败");
        }
        return bizRegion.getBizRegionId();
    }

    private void valiadateAddBizRegionDuplicate(BizRegionParam bizRegionParam) throws DtvsAdminException {
        List<BizRegion> bizRegionList = list(Wrappers.<BizRegion>lambdaQuery().eq(BizRegion::getBizRegionName, bizRegionParam.getBizRegionName()).
                eq(BizRegion::getProjectId, bizRegionParam.getProjectId()).eq(BizRegion::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(bizRegionList)) {
            throw new DtvsAdminException("业务域名称重复");
        }
    }

    private void validateAddBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException {
        if (Objects.isNull(bizRegionParam)) {
            throw new DtvsAdminException("业务域参数为空");
        }
        if (Objects.isNull(bizRegionParam.getProjectId())) {
            throw new DtvsAdminException("项目ID为空");
        }
        if (StringUtils.isBlank(bizRegionParam.getBizRegionName())) {
            throw new DtvsAdminException("业务域名称为空");
        }
        if (bizRegionParam.getBizRegionName().length() > 50) {
            throw new DtvsAdminException("业务域长度超过50");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException {
        validateEditBizRegion(bizRegionParam);
        Long bizRegionId = saveOrUpdateBizRegion(bizRegionParam);
        return bizRegionId;
    }

    private void validateEditBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException {
        BizRegion bizRegion;
        validateAddBizRegion(bizRegionParam);
        if (Objects.isNull(bizRegionParam.getBizRegionId())) {
            throw new DtvsAdminException("业务域ID为空");
        }
        if (Objects.isNull(bizRegion = getById(bizRegionParam.getBizRegionId()))) {
            throw new DtvsAdminException("业务域不存在");
        }
        List<BizRegion> bizRegionList = list(Wrappers.<BizRegion>lambdaQuery().eq(BizRegion::getBizRegionName, bizRegionParam.getBizRegionName()).
                eq(BizRegion::getProjectId, bizRegionParam.getProjectId()).eq(BizRegion::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(bizRegionList)) {
            if (bizRegionList.stream().filter(b -> !b.getBizRegionId().equals(bizRegion.getBizRegionId())).count() > 0) {
                throw new DtvsAdminException("业务域名称重复");
            }
        }
    }

    @Override
    public BizRegionVO detail(Long bizRegionId) throws DtvsAdminException {
        BizRegion bizRegion = validateBizRegionExistsById(bizRegionId);
        // todo 业务域设计未完成 业务处理待定
        BizRegionVO bizRegionVO = mapperFactory.getMapperFacade().map(bizRegion, BizRegionVO.class);
        return bizRegionVO;
    }

    private BizRegion validateBizRegionExistsById(Long bizRegionId) throws DtvsAdminException {
        BizRegion bizRegion;
        if (Objects.isNull(bizRegionId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(bizRegion = getOne(Wrappers.<BizRegion>lambdaQuery().eq(BizRegion::getBizRegionId, bizRegionId).eq(BizRegion::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("业务域不存在");
        }

        return bizRegion;
    }

    @Override
    public Boolean delete(Long bizRegionId) throws DtvsAdminException {
        BizRegion bizRegion = validateBizRegionExistsById(bizRegionId);
        BizRegion updateBizRegion = new BizRegion();
        updateBizRegion.setBizRegionId(bizRegionId);
        updateBizRegion.setIsDeleted(IsDeletedEnum.YES.getValue());
        if (!saveOrUpdate(updateBizRegion)) {
            throw new DtvsAdminException("删除业务域失败");
        }
        return Boolean.TRUE;
    }
}
