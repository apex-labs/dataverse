package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageProjectParam;
import org.apex.dataverse.param.ProjectParam;
import org.apex.dataverse.service.IProjectService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.ProjectVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/project")
@Api(tags = "项目接口")
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @PostMapping("/page")
    @ApiOperation(value = "项目列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = PageProjectParam.class)})
    public ResultVO<PageResult<ProjectVO>> page(@RequestBody PageProjectParam pageProjectParam) throws DtvsAdminException {
        return ResultVO.success(projectService.pageProject(pageProjectParam));
    }

    @PostMapping("/addProject")
    @ApiOperation(value = "新增项目")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存参数", dataTypeClass = ProjectParam.class)})
    public ResultVO<Long> addProject(@RequestBody ProjectParam projectParam) throws DtvsAdminException {
        return ResultVO.success(projectService.addProject(projectParam));
    }

    @PostMapping("/editProject")
    @ApiOperation(value = "编辑项目")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑参数", dataTypeClass = ProjectParam.class)})
    public ResultVO<Long> editProject(@RequestBody ProjectParam projectParam) throws DtvsAdminException {
        return ResultVO.success(projectService.editProject(projectParam));
    }

    @GetMapping("detail/{projectId}")
    @ApiOperation(("项目详情"))
    public ResultVO<ProjectVO> detail(@PathVariable("projectId") Long projectId) throws DtvsAdminException {
        return ResultVO.success(projectService.detail(projectId));
    }

    @GetMapping("delete/{projectId}")
    @ApiOperation(("删除项目"))
    public ResultVO<Boolean> delete(@PathVariable("projectId") Long projectId) throws DtvsAdminException {
        return ResultVO.success(projectService.delete(projectId));
    }

}
