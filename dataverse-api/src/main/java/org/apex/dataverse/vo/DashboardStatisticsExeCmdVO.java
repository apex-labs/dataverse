package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DashboardStatisticsExeCmdVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:46
 */
@Data
public class DashboardStatisticsExeCmdVO {

    @ApiModelProperty("失败命令数量")
    private Integer failCount;

    @ApiModelProperty("成功命令数量")
    private Integer successCount;

    @ApiModelProperty("展示时间")
    private String date;
}
