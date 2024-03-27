package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScheduleInstanceParam {

    @ApiModelProperty("调度执行实例ID")
    private Long scheduleInstanceId;

    @ApiModelProperty("调度实例编码，每次调度生成一 个唯一编码")
    private String scheduleInstanceCode;

    @ApiModelProperty("调度任务编码")
    private String scheduleCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("截止偏移量")
    private String offset;

    @ApiModelProperty("运行状态")
    private Integer exeStatus;


}
