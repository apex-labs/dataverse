package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.entity.Project;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.ProjectMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.PageProjectParam;
import org.apex.dataverse.param.ProjectParam;
import org.apex.dataverse.service.IProjectService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.ProjectVO;
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
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public PageResult<ProjectVO> pageProject(PageProjectParam pageProjectParam) throws DtvsAdminException {
        validateProject(pageProjectParam);
        IPage<Project> iPage = getIPage(pageProjectParam);
        PageResult<ProjectVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), ProjectVO.class));
        return pageResult;
    }

    private IPage<Project> getIPage(PageProjectParam pageProjectParam) {
        //默认单页10条记录
        Page<Project> page = new Page<>();
        PageQueryParam pageQueryParam = pageProjectParam.getPageQueryParam();
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

        LambdaQueryWrapper<Project> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(pageProjectParam.getProjectName())) {
            queryWrapper.eq(Project::getProjectName, pageProjectParam.getProjectName());
        }
        queryWrapper.eq(Project::getIsDeleted, IsDeletedEnum.NO.getValue());
        return page(page, queryWrapper);
    }

    private void validateProject(PageProjectParam pageProjectParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageProjectParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageProjectParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addProject(ProjectParam projectParam) throws DtvsAdminException {
        validateAddProject(projectParam);
        validateAddProjectDuplicates(projectParam);
        // 新增项目
        Long projectId = saveOrUpdateProject(projectParam);
        return projectId;
    }

    private void validateAddProjectDuplicates(ProjectParam projectParam) throws DtvsAdminException {
        List<Project> projectList = list(Wrappers.<Project>lambdaQuery().eq(Project::getProjectName, projectParam.getProjectName()).
                eq(Project::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()).eq(Project::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(projectList)) {
            throw new DtvsAdminException("项目名称重复");
        }
    }

    private Long saveOrUpdateProject(ProjectParam projectParam) throws DtvsAdminException {
        Project project = mapperFactory.getMapperFacade().map(projectParam, Project.class);
        if (Objects.isNull(projectParam.getProjectId())) {
            project.setCreateTime(LocalDateTime.now());
        }
        project.setUpdateTime(LocalDateTime.now());
        project.setIsDeleted(IsDeletedEnum.NO.getValue());
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        project.setTenantId(userInfo.getTenantId());
        project.setTenantName(userInfo.getTenantName());
        project.setDeptId(userInfo.getDeptId());
        project.setDeptName(userInfo.getDeptName());
        project.setUserId(userInfo.getUserId());
        project.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(project)) {
            throw new DtvsAdminException("保存项目失败");
        }
        return project.getProjectId();
    }

    private void validateAddProject(ProjectParam projectParam) throws DtvsAdminException {
        if (Objects.isNull(projectParam)) {
            throw new DtvsAdminException("项目参数为空");
        }
        if (StringUtils.isBlank(projectParam.getProjectName())) {
            throw new DtvsAdminException("项目名称为空");
        }
        if (projectParam.getProjectName().length() > 50) {
            throw new DtvsAdminException("项目名称为空");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editProject(ProjectParam projectParam) throws DtvsAdminException {
        validateEditProject(projectParam);
        // 编辑项目
        Long projectId = saveOrUpdateProject(projectParam);
        return projectId;
    }

    private void validateEditProject(ProjectParam projectParam) throws DtvsAdminException {
        Project project;
        validateAddProject(projectParam);
        if (Objects.isNull(projectParam.getProjectId())) {
            throw new DtvsAdminException("项目ID为空");
        }
        if (Objects.isNull(project = getOne(Wrappers.<Project>lambdaQuery().eq(Project::getProjectId,projectParam.getProjectId()).eq(Project::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("项目不存在");
        }
        List<Project> projectList = list(Wrappers.<Project>lambdaQuery().eq(Project::getProjectName, projectParam.getProjectName()).
                eq(Project::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()).eq(Project::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(projectList)) {
            if (projectList.stream().filter(p -> !p.getProjectId().equals(project.getProjectId())).count() > 0)  {
                throw new DtvsAdminException("项目名称重复");
            }
        }
    }

    @Override
    public ProjectVO detail(Long projectId) throws DtvsAdminException {
        Project  project = validateProjectExistsById(projectId);
        // todo 项目的规划暂时空缺 业务逻辑处理待定
        ProjectVO projectVO = mapperFactory.getMapperFacade().map(project, ProjectVO.class);
        return projectVO;
    }

    private Project validateProjectExistsById(Long projectId) throws DtvsAdminException {
        Project project;
        if (Objects.isNull(projectId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(project = getOne(Wrappers.<Project>lambdaQuery().eq(Project::getProjectId, projectId).eq(Project::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("项目不存在");
        }

        return project;
    }

    @Override
    public Boolean delete(Long projectId) throws DtvsAdminException {
        Project  project = validateProjectExistsById(projectId);
        Project updateProject = new Project();
        updateProject.setProjectId(projectId);
        updateProject.setIsDeleted(IsDeletedEnum.YES.getValue());
        if (!saveOrUpdate(updateProject)) {
            throw new DtvsAdminException("删除项目失败");
        }
        return Boolean.TRUE;
    }
}
