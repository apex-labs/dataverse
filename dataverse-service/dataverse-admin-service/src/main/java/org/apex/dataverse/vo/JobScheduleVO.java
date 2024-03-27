package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@ApiModel
@Data
public class JobScheduleVO {

    @ApiModelProperty("调度任务主键")
    private Long scheduleId;

    @ApiModelProperty("调度任务编码")
    private String scheduleCode;

    @ApiModelProperty("调度名称")
    private String scheduleName;

    @ApiModelProperty("空间编码")
    private String dvsCode;

    @ApiModelProperty("空间环境, 0:Basic, 1:Dev, 2:Prod")
    private Integer env;

    @ApiModelProperty("调度任务cron表达式")
    private String cron;

    @ApiModelProperty("调度计划生命周期/状态  调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer scheduleLifecycle;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("开始时间")
    private LocalDateTime createTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("调度任务DAG对象")
    private String rawData;

    @ApiModelProperty("调度任务ID")
    private Long jobId;

    /**
     * 调度起始时间
     */
    private LocalDateTime startTime;

    /**
     * 调度结束时间
     */
    private LocalDateTime endTime;

    /**
     * 调度起始时间
     */
    private String startTimeValue;

    /**
     * 调度结束时间
     */
    private String endTimeValue;


    @ApiModelProperty("间隔几天")
    private String dayNum;

    @ApiModelProperty("频率 指定时间")
    private String time;

}
