package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: SaveJobScheduleParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/7 10:58
 */
@Data
@ApiModel
public class SaveJobScheduleParam {

    @ApiModelProperty("调度作业主键")
    private Long scheduleId;

    @ApiModelProperty("调度编码，同一调度在不同环境中编码相同")
    private String scheduleCode;

    @ApiModelProperty("调度作业名称")
    private String scheduleName;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("定时规则，cron表达式")
    private String cron;

    @ApiModelProperty("调度计划生命周期/状态  调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer scheduleLifecycle;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("间隔几天")
    private String dayNum;

    @ApiModelProperty("频率 指定时间")
    private String time;

}
