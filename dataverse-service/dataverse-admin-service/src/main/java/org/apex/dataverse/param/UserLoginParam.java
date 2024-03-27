package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: UserLoginParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/20 10:41
 */
@Data
public class UserLoginParam {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("登录用户类型, 1:试用账户登录")
    private Integer loginType;
}
