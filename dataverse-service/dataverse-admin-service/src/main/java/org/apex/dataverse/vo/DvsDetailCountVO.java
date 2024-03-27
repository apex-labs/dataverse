package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DvsDetailCountVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/10 15:34
 */
@Data
@ApiModel
public class DvsDetailCountVO {

    @ApiModelProperty("数据空间开发模式下数据集成数量")
    private Integer devEtlJobCount;
    @ApiModelProperty("数据空间生产模式下数据集成数量")
    private Integer prodEtlJobCount;
    @ApiModelProperty("数据空间独立模式下数据集成数量")
    private Integer basicEtlJobCount;

    @ApiModelProperty("数据空间开发模式下数据开发数量")
    private Integer devBdmJobCount;
    @ApiModelProperty("数据空间生产模式下数据开发数量")
    private Integer prodBdmJobCount;
    @ApiModelProperty("数据空间独立模式下数据开发数量")
    private Integer basicBdmJobCount;

    @ApiModelProperty("数据空间开发模式下失败的数据集成数量")
    private Integer devEtlJobFailCount;
    @ApiModelProperty("数据空间生产模式下失败的数据集成数量")
    private Integer prodEtlJobFailCount;
    @ApiModelProperty("数据空间独立模式下失败的数据集成数量")
    private Integer basicEtlJobFailCount;

    @ApiModelProperty("数据空间开发模式下失败的数据开发数量")
    private Integer devBdmJobFailCount;
    @ApiModelProperty("数据空间生产模式下失败的数据开发数量")
    private Integer prodBdmJobFailCount;
    @ApiModelProperty("数据空间独立模式下失败的数据开发数量")
    private Integer basicBdmJobFailCount;

    @ApiModelProperty("数据空间开发模式下表数量")
    private Integer devTableCount;
    @ApiModelProperty("数据空间生产模式下表数量")
    private Integer prodTableCount;
    @ApiModelProperty("数据空间独立模式下表数量")
    private Integer basicTableCount;
}
