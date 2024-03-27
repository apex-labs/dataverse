package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeployParam {

    @ApiModelProperty("调度发布Id")
    private Long deployId;

    @ApiModelProperty("调度任务编码")
    private String scheduleCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("描述")
    private String description;

}
