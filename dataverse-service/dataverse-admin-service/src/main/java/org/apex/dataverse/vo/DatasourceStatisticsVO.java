package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DatasourceStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:09
 */
@Data
public class DatasourceStatisticsVO {

    @ApiModelProperty("数据源数量")
    private Integer dataSourceCount;

    @ApiModelProperty("basic环境数据源数量")
    private Integer basicDataSourceCount;

    @ApiModelProperty("dev环境数据源数量")
    private Integer devDataSourceCount;

    @ApiModelProperty("prod环境数据源数量")
    private Integer prodDataSourceCount;
}
