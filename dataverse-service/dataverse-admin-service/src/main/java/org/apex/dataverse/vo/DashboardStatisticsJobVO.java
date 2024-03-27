package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DashboardStatisticsJobVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:45
 */
@Data
public class DashboardStatisticsJobVO {

    @ApiModelProperty("basic环境失败作业数量")
    private Integer basicFailJobCount;

    @ApiModelProperty("dev环境失败作业数量")
    private Integer devFailJobCount;

    @ApiModelProperty("prod环境失败作业数量")
    private Integer prodFailJobCount;

    @ApiModelProperty("basic环境成功作业数量")
    private Integer basicSuccessJobCount;

    @ApiModelProperty("dev环境成功作业数量")
    private Integer devSuccessJobCount;

    @ApiModelProperty("prod环境成功作业数量")
    private Integer prodSuccessJobCount;

    @ApiModelProperty("日期")
    private String date;
}
