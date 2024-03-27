package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: EtlJobTableParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/24 10:47
 */
@Data
@ApiModel
public class EtlJobTableParam {

    @ApiModelProperty("数据集成来源表,输入")
    private TableExtractParam tableExtractParam;

    @ApiModelProperty("数据集成表转换")
    private List<TableTransformParam> tableTransformParamList;

    @ApiModelProperty("数据集成表抽取信息,输出")
    private TableLoadParam tableLoadParam;

    @ApiModelProperty("数据集成表")
    private DvsTableParam dvsTableParam;
}
