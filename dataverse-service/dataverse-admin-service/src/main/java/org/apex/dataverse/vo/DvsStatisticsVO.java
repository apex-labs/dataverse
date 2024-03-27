package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DvsStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:07
 */
@Data
public class DvsStatisticsVO {

    @ApiModelProperty("数据空间数量")
    private Integer dvsCount;

    @ApiModelProperty("basic环境数据空间数量")
    private Integer basicDvsCount;

    @ApiModelProperty("dev环境数据空间数量")
    private Integer devDvsCount;

    @ApiModelProperty("prod环境数据空间数量")
    private Integer prodDvsCount;
}
