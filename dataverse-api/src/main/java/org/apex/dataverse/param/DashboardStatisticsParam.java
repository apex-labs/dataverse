package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DashboardStatisticsParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:36
 */
@Data
public class DashboardStatisticsParam {

    @ApiModelProperty("查询开始时间:yyyy-MM-dd")
    private String startTime;

    @ApiModelProperty("查询结束时间:yyyy-MM-dd")
    private String endTime;
}
