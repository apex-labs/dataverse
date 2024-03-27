package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SaveNodeJobs
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/15 16:46
 */
@Data
@ApiModel
public class SaveNodeJobs {

    @ApiModelProperty("调度实例编码")
    private String scheduleInstanceCode;

    @ApiModelProperty("调度任务点集中的作业参数")
    private List<NodeJobsParam> nodeJobsParamList;
}
