package org.apex.dataverse.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PortGroupPageParam;
import org.apex.dataverse.param.PortGroupParam;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.PortGroup;
import org.apex.dataverse.port.service.IDvsPortService;
import org.apex.dataverse.port.service.IPortGroupService;
import org.apex.dataverse.storage.entity.Storage;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.util.UCodeUtil;
import org.apex.dataverse.vo.PortGroupVO;
import org.apex.dataverse.vo.StorageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName: PortGroupDAO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 16:27
 */
@Service
public class PortGroupDAO {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IPortGroupService portGroupService;

    @Autowired
    private IDvsPortService dvsPortService;

    public PageResult<PortGroupVO> pageList(PortGroupPageParam portGroupPageParam) throws DtvsManageException {
        validatePageGroup(portGroupPageParam);
        IPage<PortGroup> iPage = getIPage(portGroupPageParam);
        PageResult<PortGroupVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), PortGroupVO.class));
        return pageResult;
    }

    private IPage<PortGroup> getIPage(PortGroupPageParam portGroupPageParam) {
        //默认单页10条记录
        Page<PortGroup> page = new Page<>();
        PageQueryParam pageQueryParam = portGroupPageParam.getPageQueryParam();
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

        LambdaQueryWrapper<PortGroup> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNoneBlank(portGroupPageParam.getGroupName())) {
            queryWrapper.like(PortGroup::getGroupName, portGroupPageParam.getGroupName());
        }
        if (StringUtils.isNoneBlank(portGroupPageParam.getGroupCode())) {
            queryWrapper.eq(PortGroup::getGroupCode, portGroupPageParam.getGroupCode());
        }

        return portGroupService.page(page, queryWrapper);
    }

    private void validatePageGroup(PortGroupPageParam portGroupPageParam) throws DtvsManageException {
        PageQueryParam pageQueryParam = portGroupPageParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsManageException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        portGroupPageParam.setPageQueryParam(pageQueryParam);
    }

    public Long add(PortGroupParam portGroupParam) throws DtvsManageException {
        validateSaveOrUpdatePortGroup(portGroupParam);
        PortGroup portGroup = mapperFactory.getMapperFacade().map(portGroupParam, PortGroup.class);
        portGroup.setCreateTime(LocalDateTime.now());
        portGroup.setGroupCode(UCodeUtil.produce());
        if (!portGroupService.saveOrUpdate(portGroup)) {
            throw new DtvsManageException("保存连接器分组失败");
        }
        return portGroup.getGroupId();
    }

    private void validateSaveOrUpdatePortGroup(PortGroupParam portGroupParam) throws DtvsManageException {
        if (Objects.isNull(portGroupParam)) {
            throw new DtvsManageException("参数为空");
        }
        if (StringUtils.isBlank(portGroupParam.getGroupName())) {
            throw new DtvsManageException("连接器参数名称参数为空");
        }
        PortGroup namePortGroup = portGroupService.getOne(Wrappers.<PortGroup>lambdaQuery().eq(PortGroup::getGroupName, portGroupParam.getGroupName()));
        if (Objects.isNull(portGroupParam.getGroupId())) {
            if (Objects.nonNull(namePortGroup)) {
                throw new DtvsManageException("连接器名称已存在");
            }
        } else {
            PortGroup portGroup = portGroupService.getById(portGroupParam.getGroupId());
            if (Objects.isNull(portGroup)) {
                throw new DtvsManageException("连接器分组不存在");
            }
            if (Objects.nonNull(namePortGroup) && namePortGroup.getGroupId().longValue() != portGroup.getGroupId().longValue() && namePortGroup.getGroupName().equals(portGroup.getGroupName())) {
                throw new DtvsManageException("连接器名称已存在");
            }
            List<DvsPort> dvsPortList = dvsPortService.list(Wrappers.<DvsPort>lambdaQuery().eq(DvsPort::getGroupCode, portGroup.getGroupCode()));
            if (CollectionUtil.isNotEmpty(dvsPortList)) {
                throw new DtvsManageException("连接器分组已经被使用,不允许编辑");
            }
        }
    }

    public Long edit(PortGroupParam portGroupParam) throws DtvsManageException {
        validateSaveOrUpdatePortGroup(portGroupParam);
        PortGroup portGroup = mapperFactory.getMapperFacade().map(portGroupParam, PortGroup.class);
        if (!portGroupService.saveOrUpdate(portGroup)) {
            throw new DtvsManageException("保存连接器分组失败");
        }
        return portGroup.getGroupId();
    }

    public PortGroupVO detail(Long groupId) throws DtvsManageException {
        PortGroup portGroup = validatePortGroupExistsById(groupId);
        return mapperFactory.getMapperFacade().map(portGroup, PortGroupVO.class);
    }

    private PortGroup validatePortGroupExistsById(Long groupId) throws DtvsManageException {
        if (Objects.isNull(groupId)) {
            throw new DtvsManageException("参数为空");
        }
        PortGroup portGroup = portGroupService.getById(groupId);
        if (Objects.isNull(portGroup)) {
            throw new DtvsManageException("连接器分组不存在");
        }
        return portGroup;
    }

    public Boolean delete(Long groupId) throws DtvsManageException {
        PortGroup portGroup = validatePortGroupExistsById(groupId);
        List<DvsPort> dvsPortList = dvsPortService.list(Wrappers.<DvsPort>lambdaQuery().eq(DvsPort::getGroupCode, portGroup.getGroupCode()));
        if (CollectionUtil.isNotEmpty(dvsPortList)) {
            throw new DtvsManageException("连接器分组已经被使用,不允许编辑");
        }
        return portGroupService.removeById(groupId);
    }
}
