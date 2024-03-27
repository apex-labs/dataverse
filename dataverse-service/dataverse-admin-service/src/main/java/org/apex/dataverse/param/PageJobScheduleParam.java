package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageJobScheduleParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/31 17:42
 */
@Data
public class PageJobScheduleParam {

    @ApiModelProperty("分页查询参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据空间编码")
    private String dvsCode;

    @ApiModelProperty("发布状态: 环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("调度计划生命周期/状态  调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer scheduleLifecycle;

    @ApiModelProperty("调度作业名称")
    private String scheduleName;
}
