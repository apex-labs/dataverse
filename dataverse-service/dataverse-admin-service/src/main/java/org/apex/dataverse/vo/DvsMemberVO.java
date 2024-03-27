package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 数据空间成员表
 * </p>
 *
 * @author danny
 * @since 2023-05-17
 */
@Data
public class DvsMemberVO {

    @ApiModelProperty("成员ID")
    private Long memberId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("Dvs父编码，同parent下Dvs在多个环境(dev/prod)下Dvs_code相同。")
    private String dvsCode;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("1:项目负责人，2：架构师，3：业务负责人，4：研发，5：数据分析师")
    private Integer role;

    @ApiModelProperty("角色名")
    private String roleName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
