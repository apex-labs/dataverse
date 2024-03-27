package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: EtlJobStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:11
 */
@Data
public class EtlJobStatisticsVO {
    
    @ApiModelProperty("集成作业数量")
    private Integer etlJobCount;

    @ApiModelProperty("basic环境集成作业数量")
    private Integer basicEtlJobCount;

    @ApiModelProperty("dev环境集成作业数量")
    private Integer devEtlJobCount;

    @ApiModelProperty("prod环境集成作业数量")
    private Integer prodEtlJobCount;
}
