package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleNodeDTO {


    @ApiModelProperty("调度作业节点ID")
    private Long nodeId;

    @ApiModelProperty("调度作业编码")
    private String scheduleCode;

    @ApiModelProperty("调度作业节点编码")
    private String nodeCode;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("1:开始节点，2：作业节点，3：结束节点")
    private Integer nodeType;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("调度任务点集中的作业参数")
    private NodeJobsDTO nodeJobsDTO;

}
