package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SaveScheduleNode
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/7 11:50
 */
@Data
@ApiModel
public class SaveScheduleNode {
    //node
    @ApiModelProperty("调度作业节点ID")
    private Long scheduleId;

    @ApiModelProperty("调度作业编码")
    private String scheduleCode;

    @ApiModelProperty("调度作业节点编码")
    private String jobCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    /**
     * 1:开始节点，2：作业节点，3：结束节点
     */
    private Integer nodeType;

    @ApiModelProperty("调度任务边界参数")
    private List<ScheduleEdgeParam> scheduleEdgeParam;

    @ApiModelProperty("调度任务点集中的作业参数")
    private List<NodeJobsParam> nodeJobsParam;

    @ApiModelProperty("当前调度是否已添加作业节点")
    private Integer isAddNode;

}
