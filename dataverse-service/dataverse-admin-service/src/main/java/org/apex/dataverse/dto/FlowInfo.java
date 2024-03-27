package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Sunshine.Xiao
 * @date 2024/1/20 13:55
 * @since 0.1.0
 */
@Data
@ApiModel(value = "流程信息")
public class FlowInfo implements Serializable {

    @ApiModelProperty(value = "活动ID")
    private Long flowId;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "活动名称")
    private String flowName;

    @ApiModelProperty(value = "活动状态")
    private Integer state;

    @ApiModelProperty(value = "活动创建时间")
    private Long createTime;

    @ApiModelProperty(value = "活动开始时间")
    private Long startTime;

    @ApiModelProperty(value = "活动结束时间")
    private Long endTime;

    public FlowInfo() {
    }

    public FlowInfo(Long flowId, String tenantId, String flowName, Integer state, Long createTime, Long startTime, Long endTime) {
        this.flowId = flowId;
        this.tenantId = tenantId;
        this.flowName = flowName;
        this.state = state;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
