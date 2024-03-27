package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DataRegionStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:08
 */
@Data
public class DataRegionStatisticsVO {

    @ApiModelProperty("数据域数量")
    private Integer dataRegionCount;

    @ApiModelProperty("basic环境数据域数量")
    private Integer basicDataRegionCount;

    @ApiModelProperty("dev环境数据域数量")
    private Integer devDataRegionCount;

    @ApiModelProperty("prod环境数据域数量")
    private Integer prodDataRegionCount;
}
