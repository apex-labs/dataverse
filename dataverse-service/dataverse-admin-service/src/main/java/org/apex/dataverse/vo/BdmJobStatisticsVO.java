package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: BdmJobStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:12
 */
@Data
public class BdmJobStatisticsVO {
    @ApiModelProperty("开发作业数量")
    private Integer bdmJobCount;

    @ApiModelProperty("basic环境开发作业数量")
    private Integer basicBdmJobCount;

    @ApiModelProperty("dev环境开发作业数量")
    private Integer devBdmJobCount;

    @ApiModelProperty("prod环境开发作业数量")
    private Integer prodBdmJobCount;
}
