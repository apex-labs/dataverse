package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.RegionMapping;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.RegionMappingMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageRegionMappingParam;
import org.apex.dataverse.param.RegionMappingParam;
import org.apex.dataverse.service.IRegionMappingService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.vo.RegionMappingVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class RegionMappingServiceImpl extends ServiceImpl<RegionMappingMapper, RegionMapping> implements IRegionMappingService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public PageResult<RegionMappingVO> pageRegionMapping(PageRegionMappingParam pageRegionMappingParam) throws DtvsAdminException {
        validateBizRegion(pageRegionMappingParam);
        IPage<RegionMapping> iPage = getIPage(pageRegionMappingParam);
        PageResult<RegionMappingVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), RegionMappingVO.class));
        return pageResult;
    }

    private IPage<RegionMapping> getIPage(PageRegionMappingParam pageRegionMappingParam) {
        // 默认单页10条记录
        Page<RegionMapping> page = new Page<>();
        PageQueryParam pageQueryParam = pageRegionMappingParam.getPageQueryParam();
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

        LambdaQueryWrapper<RegionMapping> queryWrapper = Wrappers.lambdaQuery();
        return page(page, queryWrapper);
    }

    private void validateBizRegion(PageRegionMappingParam pageRegionMappingParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageRegionMappingParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageRegionMappingParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addRegionMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException {
        validateAddReginMapping(regionMappingParam);
        Long regionMappingId = saveOrUpdateRegionMapping(regionMappingParam);
        return regionMappingId;
    }

    private Long saveOrUpdateRegionMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException {
        RegionMapping regionMapping = mapperFactory.getMapperFacade().map(regionMappingParam, RegionMapping.class);
        if (Objects.isNull(regionMappingParam.getRegionMappingId())) {
            regionMapping.setCreateTime(LocalDateTime.now());
        }
        regionMapping.setUpdateTime(LocalDateTime.now());
        if (!saveOrUpdate(regionMapping)) {
            throw new DtvsAdminException("保存业务域数据域映射失败");
        }
        return regionMapping.getRegionMappingId();
    }

    private void validateAddReginMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException {
        if (Objects.isNull(regionMappingParam)) {
            throw new DtvsAdminException("业务域映射为空");
        }
        if (Objects.isNull(regionMappingParam.getBizRegionId())) {
            throw new DtvsAdminException("业务域ID为空");
        }
        if (Objects.isNull(regionMappingParam.getDataRegionId())) {
            throw new DtvsAdminException("数据域ID为空");
        }
        // todo 是否有其他的逻辑校验目前不知道
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editRegionMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException {
        validateEditReginMapping(regionMappingParam);
        Long regionMappingId = saveOrUpdateRegionMapping(regionMappingParam);
        return regionMappingId;
    }

    private void validateEditReginMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException {
        RegionMapping regionMapping;
        validateAddReginMapping(regionMappingParam);
        if (Objects.isNull(regionMappingParam.getRegionMappingId())) {
            throw new DtvsAdminException("业务域数据域映射ID为空");
        }
        if (Objects.isNull(regionMapping = getById(regionMappingParam.getRegionMappingId()))) {
            throw new DtvsAdminException("业务域数据域映射不存在");
        }
        // todo 是否有其他的逻辑校验目前不知道
    }
}
