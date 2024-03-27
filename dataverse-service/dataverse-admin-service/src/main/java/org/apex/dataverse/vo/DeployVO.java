package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeployVO {

    @ApiModelProperty("调度发布Id")
    private Long deployId;

    @ApiModelProperty("调度任务编码")
    private String scheduleCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("发布时间")
    private LocalDateTime deployTime;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;

    @ApiModelProperty("描述")
    private String description;

}
