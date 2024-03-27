package org.apex.dataverse.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.UserLoginParam;
import org.apex.dataverse.service.IAdminLoginService;
import org.apex.dataverse.util.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: AdminLoginController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/20 10:38
 */
@RestController
@RequestMapping("/admin/login")
public class AdminLoginController {

    @Autowired
    private IAdminLoginService adminLoginService;

    @PostMapping("/userLogin")
    @ApiOperation("用户登录接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = UserLoginParam.class)})
    public ResultVO<String> userLogin(@RequestBody UserLoginParam userLoginParam) throws DtvsAdminException {
        return ResultVO.success(adminLoginService.userLogin(userLoginParam));
    }

    @PostMapping("/validateLoginToken/{token}")
    @ApiOperation("用户登录接口")
    public ResultVO<Boolean> validateLoginToken(@PathVariable("token") String token) {
        return ResultVO.success(adminLoginService.validateLoginToken(token));
    }
}
