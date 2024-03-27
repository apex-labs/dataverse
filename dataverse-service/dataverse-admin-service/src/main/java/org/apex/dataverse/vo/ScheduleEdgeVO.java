package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleEdgeVO {

    @ApiModelProperty("边ID")
    private Long edgeId;

    @ApiModelProperty("边编码")
    private String edgeCode;

    @ApiModelProperty("调度作业编码")
    private String scheduleCode;

    @ApiModelProperty("From节点的code")
    private String fromNode;

    @ApiModelProperty("To节点的code")
    private String toNode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
