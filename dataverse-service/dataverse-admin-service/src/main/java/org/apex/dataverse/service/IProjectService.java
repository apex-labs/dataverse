package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.Project;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageProjectParam;
import org.apex.dataverse.param.ProjectParam;
import org.apex.dataverse.vo.ProjectVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IProjectService extends IService<Project> {

    /**
     * 分页查询项目
     * @param pageProjectParam
     * @return
     */
    PageResult<ProjectVO> pageProject(PageProjectParam pageProjectParam) throws DtvsAdminException;

    /**
     * 新增项目
     * @param projectParam
     * @return
     */
    Long addProject(ProjectParam projectParam) throws DtvsAdminException;

    /**
     * 编辑项目
     * @param projectParam
     * @return
     */
    Long editProject(ProjectParam projectParam) throws DtvsAdminException;

    /**
     * 项目详情
     * @param projectId
     * @return
     */
    ProjectVO detail(Long projectId) throws DtvsAdminException;

    /**
     * 删除项目
     * @param projectId
     * @return
     */
    Boolean delete(Long projectId) throws DtvsAdminException;
}
