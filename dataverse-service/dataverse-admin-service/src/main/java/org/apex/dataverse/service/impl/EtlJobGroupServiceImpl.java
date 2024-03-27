package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.DataRegion;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.EtlJob;
import org.apex.dataverse.entity.EtlJobGroup;
import org.apex.dataverse.enums.JobGroupLevelEnum;
import org.apex.dataverse.mapper.DataRegionMapper;
import org.apex.dataverse.mapper.EtlJobGroupMapper;
import org.apex.dataverse.mapper.EtlJobMapper;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.EtlJobGroupParam;
import org.apex.dataverse.param.EtlJobGroupTreeParam;
import org.apex.dataverse.service.IEtlJobGroupService;
import org.apex.dataverse.utils.CategoryCodeUtil;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.EtlJobGroupTreeVO;
import org.apex.dataverse.vo.EtlJobGroupVO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.vo.EtlJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
@Service
public class EtlJobGroupServiceImpl extends ServiceImpl<EtlJobGroupMapper, EtlJobGroup> implements IEtlJobGroupService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private EtlJobMapper etlJobMapper;

    @Autowired
    private DataRegionMapper dataRegionMapper;

    @Override
    public List<EtlJobGroupVO> listEtlJobGroup() {
        List<EtlJobGroupVO> etlJobGroupVOList = new ArrayList<>();
        List<EtlJobGroupVO> list = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<EtlJobGroup>lambdaQuery().
                eq(EtlJobGroup::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId())), EtlJobGroupVO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<Integer, List<EtlJobGroupVO>> groupMap = list.stream().collect(Collectors.groupingBy(EtlJobGroupVO::getDatasourceTypeId));
            if (CollectionUtil.isNotEmpty(groupMap)) {
                for (Map.Entry<Integer, List<EtlJobGroupVO>> entry : groupMap.entrySet()) {
                    etlJobGroupVOList.add(buildEtlJobGroupVOTree(entry.getKey(), entry.getValue()));
                }
            }
        }
        return etlJobGroupVOList;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long saveEtlJobGroup(EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateSaveEtlJobGroup(etlJobGroupParam);
        Long etlGroupId = saveOrUpdateEtlJobGroup(etlJobGroupParam, userInfo);
        return etlGroupId;
    }

    private Long saveOrUpdateEtlJobGroup(EtlJobGroupParam etlJobGroupParam, UserInfo userInfo) throws DtvsAdminException {
        EtlJobGroup etlJobGroup = mapperFactory.getMapperFacade().map(etlJobGroupParam, EtlJobGroup.class);
        if (Objects.isNull(etlJobGroup.getEtlGroupId())) {
            etlJobGroup.setCreateTime(LocalDateTime.now());
        }
        etlJobGroup.setUpdateTime(LocalDateTime.now());
        etlJobGroup.setTenantId(userInfo.getTenantId());
        etlJobGroup.setTenantName(userInfo.getTenantName());
        etlJobGroup.setDeptId(userInfo.getDeptId());
        etlJobGroup.setDeptName(userInfo.getDeptName());
        etlJobGroup.setUserId(userInfo.getUserId());
        etlJobGroup.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(etlJobGroup)) {
            throw new DtvsAdminException("保存集成分组失败");
        }
        return etlJobGroup.getEtlGroupId();
    }

    private void validateSaveEtlJobGroup(EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException {
        if (Objects.isNull(etlJobGroupParam)) {
            throw new DtvsAdminException("集成分组参数为空");
        }
        if (Objects.isNull(etlJobGroupParam.getGroupLevel())) {
            throw new DtvsAdminException("集成分组层级为空");
        }
        if (StringUtils.isBlank(etlJobGroupParam.getEtlGroupName())) {
            throw new DtvsAdminException("集成分组名称为空");
        }
        if (StringUtils.isBlank(etlJobGroupParam.getDataRegionCode())) {
            throw new DtvsAdminException("数据域编码为空");
        }
        if (CollectionUtil.isEmpty(dataRegionMapper.selectList(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getDataRegionCode, etlJobGroupParam.getDataRegionCode())))) {
            throw new DtvsAdminException("数据域不存在");
        }
        List<EtlJobGroup> etlJobGroupList = list(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getEtlGroupName, etlJobGroupParam.getEtlGroupName()));
        if (CollectionUtil.isNotEmpty(etlJobGroupList)) {
            throw new DtvsAdminException("集成分组名称重复");
        }
        if (StringUtils.isBlank(etlJobGroupParam.getParentEtlGroupCode())) {
            etlJobGroupParam.setEtlGroupCode(CategoryCodeUtil.levelOne(JobGroupLevelEnum.ONE.getValue()));
        }
        if (StringUtils.isNotBlank(etlJobGroupParam.getParentEtlGroupCode())) {
            etlJobGroupList = list(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getEtlGroupCode, etlJobGroupParam.getParentEtlGroupCode()));
            if (CollectionUtil.isEmpty(etlJobGroupList)) {
                throw new DtvsAdminException("集成分组父级编码不存在");
            }
            Integer level = etlJobGroupParam.getGroupLevel() - 1;
            if (!level.equals(etlJobGroupList.get(0).getGroupLevel())) {
                throw new DtvsAdminException("数据分组层级和父级层级不匹配");
            }
//            if (!etlJobGroupParam.getDatasourceTypeId().equals(etlJobGroupList.get(0).getDatasourceTypeId())) {
//                throw new DtvsAdminException("数据分组数据源类型和父级数据源类型不匹配");
//            }
            etlJobGroupList = list(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getEtlGroupCode, etlJobGroupParam.getParentEtlGroupCode()).orderByDesc(EtlJobGroup::getEtlGroupId));
            if (CollectionUtil.isEmpty(etlJobGroupList)) {
                etlJobGroupParam.setEtlGroupCode(CategoryCodeUtil.nextCode(etlJobGroupParam.getParentEtlGroupCode()));
            } else {
                etlJobGroupParam.setEtlGroupCode(CategoryCodeUtil.nextCode(etlJobGroupList.get(0).getEtlGroupCode()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editEtlJobGroup(EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateEditEtlJobGroup(etlJobGroupParam);
        EtlJobGroupParam updateEtlJobGroupParam = new EtlJobGroupParam();
        updateEtlJobGroupParam.setEtlGroupId(etlJobGroupParam.getEtlGroupId());
        updateEtlJobGroupParam.setEtlGroupName(etlJobGroupParam.getEtlGroupName());
        Long etlGroupId = saveOrUpdateEtlJobGroup(updateEtlJobGroupParam, userInfo);
        return etlGroupId;
    }

    private void validateEditEtlJobGroup(EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException {
        if (Objects.isNull(etlJobGroupParam)) {
            throw new DtvsAdminException("集成分组参数为空");
        }
        if (Objects.isNull(etlJobGroupParam.getEtlGroupId())) {
            throw new DtvsAdminException("集成分组ID为空");
        }
        if (StringUtils.isBlank(etlJobGroupParam.getEtlGroupName())) {
            throw new DtvsAdminException("集成分组名称为空");
        }
        if (Objects.isNull(getById(etlJobGroupParam.getEtlGroupId()))) {
            throw new DtvsAdminException("集成分组不存在");
        }
        List<EtlJobGroup> etlJobGroupList = list(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getEtlGroupName, etlJobGroupParam.getEtlGroupName()));
        if (CollectionUtil.isNotEmpty(etlJobGroupList)) {
            if (etlJobGroupList.stream().filter(e -> !e.getEtlGroupId().equals(etlJobGroupParam.getEtlGroupId())).count() > 0) {
                throw new DtvsAdminException("集成分组名称重复");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long deleteEtlJobGroup(Long etlGroupId) throws DtvsAdminException {
        validateDeleteEtlJobGroup(etlGroupId);
        if (!removeById(etlGroupId)) {
            throw new DtvsAdminException("删除集成分组失败");
        }
        return etlGroupId;
    }

    private void validateDeleteEtlJobGroup(Long etlGroupId) throws DtvsAdminException {
        EtlJobGroup etlJobGroup;
        if (Objects.isNull(etlGroupId)) {
            throw new DtvsAdminException("分组ID为空");
        }
        if (Objects.isNull(etlJobGroup = getById(etlGroupId))) {
            throw new DtvsAdminException("集成分组不存在");
        }
        if (CollectionUtil.isNotEmpty(etlJobMapper.selectList(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlGroupCode, etlJobGroup.getEtlGroupCode())))) {
            throw new DtvsAdminException("集成分组被使用，不能删除");
        }
    }

    @Override
    public EtlJobGroupVO listByDatasourceTypeId(Integer datasourceTypeId) {
        List<EtlJobGroupVO> list = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getDatasourceTypeId, datasourceTypeId).
                eq(EtlJobGroup::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId())), EtlJobGroupVO.class);
        return buildEtlJobGroupVOTree(datasourceTypeId, list);
    }

    private EtlJobGroupVO buildEtlJobGroupVOTree(Integer datasourceTypeId, List<EtlJobGroupVO> list) {
        EtlJobGroupVO rootNodeTree = buildEtlJobGroupTreeRootNode(datasourceTypeId);
        if (CollectionUtil.isNotEmpty(list)) {
            List<EtlJobGroupVO> rootChildNode = list.stream().filter(e -> StringUtils.isBlank(e.getParentEtlGroupCode())).collect(Collectors.toList());
            List<EtlJobGroupVO> childNode = list.stream().filter(e -> StringUtils.isNotBlank(e.getParentEtlGroupCode())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(rootChildNode) && CollectionUtil.isNotEmpty(childNode)) {
                Map<String, List<EtlJobGroupVO>> childNodeMap = childNode.stream().collect(Collectors.groupingBy(EtlJobGroupVO::getParentEtlGroupCode));
                list.forEach(e -> {
                    e.setChildren(childNodeMap.get(e.getEtlGroupCode()));
                });
            }
            rootNodeTree.setChildren(rootChildNode);
        }
        return rootNodeTree;
    }

    private EtlJobGroupVO buildEtlJobGroupTreeRootNode(Integer datasourceTypeId) {
        EtlJobGroupVO rootNodeTree = new EtlJobGroupVO();
        rootNodeTree.setDatasourceTypeId(datasourceTypeId);
        return rootNodeTree;
    }

    private void buildEtlJobGroupTree(EtlJobGroupVO rootNode, List<EtlJobGroupVO> etlJobGroupVOList) {
        List<EtlJobGroupVO> etlJobGroupVOListNew = etlJobGroupVOList.stream().filter(e -> StringUtils.isNotEmpty(e.getParentEtlGroupCode()) && e.getParentEtlGroupCode().equals(rootNode.getEtlGroupCode()))
                .collect(Collectors.toList());
        rootNode.setChildren(etlJobGroupVOListNew);
        if (CollectionUtil.isNotEmpty(etlJobGroupVOListNew)) {
            for (EtlJobGroupVO etlJobGroupVO : etlJobGroupVOListNew) {
                buildEtlJobGroupTree(etlJobGroupVO, etlJobGroupVOList);
            }
        }
    }

    @Override
    public EtlJobGroupTreeVO etlJobGroupTreeByDataRegion(EtlJobGroupTreeParam etlJobTreeParam) throws DtvsAdminException {
        validateEtlJobGroupTreeByDataRegion(etlJobTreeParam);
        EtlJobGroupTreeVO etlJobGroupTreeVO = new EtlJobGroupTreeVO();
        List<EtlJobGroup> etlJobGroupList = list(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getDataRegionCode, etlJobTreeParam.getDataRegionCode()));
        List<EtlJob> etlJobList = etlJobMapper.selectList(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getDataRegionCode, etlJobTreeParam.getDataRegionCode()).
                eq(EtlJob::getEnv, etlJobTreeParam.getEnv()).eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()).
                eq(EtlJob::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(etlJobGroupList)) {
            etlJobGroupTreeVO.setRootDataRegionCode(etlJobTreeParam.getDataRegionCode());
            etlJobGroupTreeVO = buildEtlJobGroupTreeVO(etlJobList, etlJobGroupList, etlJobGroupTreeVO);
        }
        return etlJobGroupTreeVO;
    }

    private EtlJobGroupTreeVO buildEtlJobGroupTreeVO(List<EtlJob> etlJobList, List<EtlJobGroup> etlJobGroupList, EtlJobGroupTreeVO rootNodeTreeVO) {
        List<EtlJobVO> etlJobVOList = mapperFactory.getMapperFacade().mapAsList(etlJobList, EtlJobVO.class);
        List<EtlJobGroupTreeVO> etlJobGroupTreeVOList = mapperFactory.getMapperFacade().mapAsList(etlJobGroupList, EtlJobGroupTreeVO.class);
        List<EtlJobGroupTreeVO> rootChildNode = etlJobGroupTreeVOList.stream().filter(e -> StringUtils.isBlank(e.getParentEtlGroupCode())).collect(Collectors.toList());
        List<EtlJobGroupTreeVO> childNode = etlJobGroupTreeVOList.stream().filter(e -> StringUtils.isNotBlank(e.getParentEtlGroupCode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(rootChildNode)) {
            Map<String, List<EtlJobVO>> etlJobMap = etlJobVOList.stream().collect(Collectors.groupingBy(EtlJobVO::getEtlGroupCode));
            Map<String, List<EtlJobGroupTreeVO>> childNodeMap;
            if (CollectionUtil.isNotEmpty(childNode)) {
                childNodeMap = childNode.stream().collect(Collectors.groupingBy(EtlJobGroupTreeVO::getParentEtlGroupCode));
            } else {
                childNodeMap = null;
            }
            etlJobGroupTreeVOList.forEach(e -> {
                e.setEtlJobVOList(etlJobMap.get(e.getEtlGroupCode()));
                if (CollectionUtil.isNotEmpty(childNodeMap)) {
                    e.setChildren(childNodeMap.get(e.getEtlGroupCode()));
                }
            });
        }
        rootNodeTreeVO.setChildren(rootChildNode);
        return rootNodeTreeVO;
    }

    private void validateEtlJobGroupTreeByDataRegion(EtlJobGroupTreeParam etlJobTreeParam) throws DtvsAdminException {
        if (Objects.isNull(etlJobTreeParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(etlJobTreeParam.getEnv())) {
            throw new DtvsAdminException("环境参数为空");
        }
        if (StringUtils.isBlank(etlJobTreeParam.getDataRegionCode())) {
            throw new DtvsAdminException("数据域编码参数为空");
        }
    }
}
