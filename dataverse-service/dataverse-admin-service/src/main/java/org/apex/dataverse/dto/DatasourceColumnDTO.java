package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DatasourceColumnDTO
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 11:35
 **/
@Data
public class DatasourceColumnDTO {

    @ApiModelProperty("表名称名称")
    private String tableName;

    @ApiModelProperty("列名称")
    private String columnName;

    @ApiModelProperty("源数据列类型")
    private String sourceColumnType;

    @ApiModelProperty("是否主键 1:是 , 2:否")
    private Integer isPk;

    @ApiModelProperty("数据源列对应数字")
    private Integer typeId;

    @ApiModelProperty("数据类型ID")
    private Integer dateTypeId;

    @ApiModelProperty("数据类型名称")
    private String dataTypeName;

    @ApiModelProperty("数据类型短类型ID")
    private Integer shortDataTypeId;

    @ApiModelProperty("数据类型短类型名称")
    private String shortDataTypeName;

    @ApiModelProperty("是否增量标记")
    private Boolean isIncrement;

}
