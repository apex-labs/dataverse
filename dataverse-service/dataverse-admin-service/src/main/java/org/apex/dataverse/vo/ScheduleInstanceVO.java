package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleInstanceVO {

    @ApiModelProperty("调度执行实例ID")
    private Long scheduleInstanceId;

    @ApiModelProperty("调度实例编码，每次调度生成一 个唯一编码schedule_code+start(yyyyMMddHHmm)+end(yyyyMMddHHmm) 如：xxx_202403160000_202403170000")
    private String scheduleInstanceCode;

    @ApiModelProperty("调度任务编码")
    private String scheduleCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("截止偏移量")
    private String offset;

    @ApiModelProperty("运行状态")
    private Integer exeStatus;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
