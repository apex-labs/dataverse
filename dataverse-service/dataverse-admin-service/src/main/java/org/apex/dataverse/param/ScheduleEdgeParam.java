package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScheduleEdgeParam {

    @ApiModelProperty("调度作业编码")
    private String scheduleCode;

    @ApiModelProperty("From节点的code")
    private String fromNode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

}
