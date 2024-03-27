package org.apex.dataverse.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("隔离信息")
@Data
public class Isolation implements Serializable {

    @ApiModelProperty("部门ID")
    private Long groupId;
    @ApiModelProperty("组织ID")
    private String orgCode;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("隔离级别 1.组织，2.用户组，3.用户")
    private Integer isolationLevel;
}
