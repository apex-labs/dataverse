package org.apex.dataverse.controller;


import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageDeployParam;
import org.apex.dataverse.service.IDeployService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DeployVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@RestController
@RequestMapping("/deploy")
@Api(tags = "调度发布相关接口")
public class DeployController {

    @Autowired
    private IDeployService deployService;

    @PostMapping("/page")
    @ApiOperation("调度发布列表展示")
    @ApiImplicitParams({@ApiImplicitParam(value = "调度发布列表请求参数", dataTypeClass = PageDeployParam.class)})
    public ResultVO<PageResult<DeployVO>> page(@RequestBody PageDeployParam pageDeployParam) {
        return ResultVO.success(deployService.pageDeployVO(pageDeployParam));
    }

}
