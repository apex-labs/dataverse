package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: EtlJobTableVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/25 15:56
 */
@Data
@ApiModel
public class EtlJobTableVO {

    @ApiModelProperty("集成任务抽取表展示")
    private TableExtractVO tableExtractVO;

    @ApiModelProperty("集成任务Load表")
    private TableLoadVO tableLoadVO;

    @ApiModelProperty("集成任务转换表")
    private List<TableTransformVO> tableTransformVOList;

    @ApiModelProperty("集成任务表")
    private DvsTableVO dvsTableVO;

    @ApiModelProperty("数据集成表统计信息")
    private TableStatisticsVO tableStatisticsVO;

    @ApiModelProperty("数据集成表存储信息")
    private TableStorageVO tableStorageVO;

    @ApiModelProperty("数据集成表建表语句信息")
    private DvsTableDdlVO dvsTableDdlVO;
}
