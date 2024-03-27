package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.BdmJob;
import org.apex.dataverse.entity.BdmJobGroup;
import org.apex.dataverse.entity.DataRegion;
import org.apex.dataverse.enums.JobGroupLevelEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.BdmJobGroupMapper;
import org.apex.dataverse.mapper.BdmJobMapper;
import org.apex.dataverse.mapper.DataRegionMapper;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.BdmJobGroupParam;
import org.apex.dataverse.service.IBdmJobGroupService;
import org.apex.dataverse.utils.CategoryCodeUtil;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.BdmJobGroupVO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import java.util.stream.Collectors;

/**
 * <p>
 * 大数据建模作业分组 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
@Service
public class BdmJobGroupServiceImpl extends ServiceImpl<BdmJobGroupMapper, BdmJobGroup> implements IBdmJobGroupService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private BdmJobMapper bdmJobMapper;

    @Autowired
    private DataRegionMapper dataRegionMapper;

    @Override
    public List<BdmJobGroupVO> listBdmJobGroup() {
        List<BdmJobGroupVO> list = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId())), BdmJobGroupVO.class);
        return buildBdmJobGroupVOTree(list);
    }

    public List<BdmJobGroupVO> buildBdmJobGroupVOTree(List<BdmJobGroupVO> list) {
//        BdmJobGroupVO rootNodeTree = new BdmJobGroupVO();
        List<BdmJobGroupVO> rootChildNode = null;
        if (CollectionUtil.isNotEmpty(list)) {
            rootChildNode = list.stream().filter(e -> StringUtils.isBlank(e.getParentBdmGroupCode())).collect(Collectors.toList());
            List<BdmJobGroupVO> childNode = list.stream().filter(e -> StringUtils.isNotBlank(e.getParentBdmGroupCode())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(rootChildNode) && CollectionUtil.isNotEmpty(childNode)) {
                Map<String, List<BdmJobGroupVO>> childNodeMap = childNode.stream().collect(Collectors.groupingBy(BdmJobGroupVO::getParentBdmGroupCode));
                list.forEach(e -> {
                    e.setChildren(childNodeMap.get(e.getBdmGroupCode()));
                });
            }
//            rootNodeTree.setChildren(rootChildNode);
        }
        return rootChildNode;
    }

    @Override
    public List<BdmJobGroupVO> listBdmJobGroupByDataRegionCode(String dataRegionCode){
//        dataRegionMapper.selectList(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getDataRegionCode, dataRegionCode));
        List<BdmJobGroupVO> list = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId())
                .eq(BdmJobGroup::getDataRegionCode, dataRegionCode)), BdmJobGroupVO.class);
        return buildBdmJobGroupVOTree(list);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer saveBdmJobGroup(BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateSaveBdmJobGroup(bdmJobGroupParam);
        Integer bdmGroupId = saveOrUpdateBdmJobGroup(bdmJobGroupParam, userInfo);
        return bdmGroupId;
    }

    private Integer saveOrUpdateBdmJobGroup(BdmJobGroupParam bdmJobGroupParam, UserInfo userInfo) throws DtvsAdminException {
        BdmJobGroup bdmJobGroup = mapperFactory.getMapperFacade().map(bdmJobGroupParam, BdmJobGroup.class);
        if (Objects.isNull(bdmJobGroup.getBdmJobGroupId())) {
            bdmJobGroup.setCreateTime(LocalDateTime.now());
        }
        bdmJobGroup.setUpdateTime(LocalDateTime.now());
        bdmJobGroup.setTenantId(userInfo.getTenantId());
        bdmJobGroup.setTenantName(userInfo.getTenantName());
        bdmJobGroup.setDeptId(userInfo.getDeptId());
        bdmJobGroup.setDeptName(userInfo.getDeptName());
        bdmJobGroup.setUserId(userInfo.getUserId());
        bdmJobGroup.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(bdmJobGroup)) {
            throw new DtvsAdminException("保存开发分组失败");
        }
        return bdmJobGroup.getBdmJobGroupId();
    }

    private void validateSaveBdmJobGroup(BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException {
        if (Objects.isNull(bdmJobGroupParam)) {
            throw new DtvsAdminException("开发分组参数为空");
        }
        if (Objects.isNull(bdmJobGroupParam.getGroupLevel())) {
            throw new DtvsAdminException("开发分组层级为空");
        }
        if(bdmJobGroupParam.getGroupLevel() > 2){
            throw new DtvsAdminException("开发分组层级最多为3层");
        }
        if (StringUtils.isBlank(bdmJobGroupParam.getBdmGroupName())) {
            throw new DtvsAdminException("开发分组名称为空");
        }
        List<BdmJobGroup> bdmJobGroupList = list(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getBdmGroupName, bdmJobGroupParam.getBdmGroupName()));
        if (CollectionUtil.isNotEmpty(bdmJobGroupList)) {
            throw new DtvsAdminException("开发分组名称重复");
        }
        if (StringUtils.isBlank(bdmJobGroupParam.getParentBdmGroupCode())) {
            bdmJobGroupParam.setBdmGroupCode(CategoryCodeUtil.levelOne(JobGroupLevelEnum.ONE.getValue()));
        }
        if (StringUtils.isNotBlank(bdmJobGroupParam.getParentBdmGroupCode())) {
            bdmJobGroupList = list(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getBdmGroupCode, bdmJobGroupParam.getParentBdmGroupCode()));
            if (CollectionUtil.isEmpty(bdmJobGroupList)) {
                throw new DtvsAdminException("开发分组父级编码不存在");
            }
            Integer level = bdmJobGroupParam.getGroupLevel() - 1;
            if (!level.equals(bdmJobGroupList.get(0).getGroupLevel())) {
                throw new DtvsAdminException("数据分组层级和父级层级不匹配");
            }

            bdmJobGroupList = list(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getParentBdmGroupCode, bdmJobGroupParam.getParentBdmGroupCode()).orderByDesc(BdmJobGroup::getBdmGroupCode));
            if (CollectionUtil.isEmpty(bdmJobGroupList)) {
                bdmJobGroupParam.setBdmGroupCode(CategoryCodeUtil.nextCode(bdmJobGroupParam.getParentBdmGroupCode()));
            } else {
                bdmJobGroupParam.setBdmGroupCode(CategoryCodeUtil.nextCode(bdmJobGroupList.get(0).getBdmGroupCode()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer editBdmJobGroup(BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateEditBdmJobGroup(bdmJobGroupParam);
        BdmJobGroupParam updateBdmJobGroupParam = new BdmJobGroupParam();
        updateBdmJobGroupParam.setBdmJobGroupId(bdmJobGroupParam.getBdmJobGroupId());
        updateBdmJobGroupParam.setBdmGroupName(bdmJobGroupParam.getBdmGroupName());
        Integer bdmGroupId = saveOrUpdateBdmJobGroup(updateBdmJobGroupParam, userInfo);
        return bdmGroupId;
    }

    private void validateEditBdmJobGroup(BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException {
        if (Objects.isNull(bdmJobGroupParam)) {
            throw new DtvsAdminException("开发分组参数为空");
        }
        if (Objects.isNull(bdmJobGroupParam.getBdmJobGroupId())) {
            throw new DtvsAdminException("开发分组ID为空");
        }
        if (StringUtils.isBlank(bdmJobGroupParam.getBdmGroupName())) {
            throw new DtvsAdminException("开发分组名称为空");
        }
        if (Objects.isNull(getById(bdmJobGroupParam.getBdmJobGroupId()))) {
            throw new DtvsAdminException("开发分组不存在");
        }
        List<BdmJobGroup> bdmJobGroupList = list(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getBdmGroupName, bdmJobGroupParam.getBdmGroupName()));
        if (CollectionUtil.isNotEmpty(bdmJobGroupList)) {
            if (bdmJobGroupList.stream().filter(e -> !e.getBdmJobGroupId().equals(bdmJobGroupParam.getBdmJobGroupId())).count() > 0) {
                throw new DtvsAdminException("开发分组名称重复");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer deleteBdmJobGroup(Integer bdmJobGroupId) throws DtvsAdminException {
        validateDeleteBdmJobGroup(bdmJobGroupId);
        if (!removeById(bdmJobGroupId)) {
            throw new DtvsAdminException("删除开发分组失败");
        }
        return bdmJobGroupId;
    }

    private void validateDeleteBdmJobGroup(Integer bdmJobGroupId) throws DtvsAdminException {
        BdmJobGroup bdmJobGroup;
        if (Objects.isNull(bdmJobGroupId)) {
            throw new DtvsAdminException("分组ID为空");
        }
        if (Objects.isNull(bdmJobGroup = getById(bdmJobGroupId))) {
            throw new DtvsAdminException("开发分组不存在");
        }
        if (CollectionUtil.isNotEmpty(bdmJobMapper.selectList(Wrappers.<BdmJob>lambdaQuery().eq(BdmJob::getBdmGroupCode, bdmJobGroup.getBdmGroupCode())))) {
            throw new DtvsAdminException("开发分组被使用，不能删除");
        }
    }
}
