package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("数据表对应的列参数")
@Data
public class DvsTableColumnParam {

    @ApiModelProperty("列ID")
    private Long columnId;

    @ApiModelProperty("列编码，同一列在不同环境中编码相同")
    private String columnCode;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty(value = " 列名，英文，帕斯卡命名法则", required = true)
    private String columnName;

    @ApiModelProperty(value = "列别名，显示名称", required = true)
    private String columnAlias;

    @ApiModelProperty(value = "环境，0：BASIC、1：DEV、2：PROD", required = true)
    private Integer env;

    @ApiModelProperty(value = "数据类型ID", required = true)
    private Integer dataTypeId;

    @ApiModelProperty(value = "数据类型名称", required = true)
    private String dataTypeName;

    @ApiModelProperty(value = "数据类型-短类型。1：字符串，2：整数，3：浮点，4：日期，5：日期时间", required = true)
    private Integer shortDataTypeId;

    @ApiModelProperty(value = "是否主键, 1:是，0:否", required = true)
    private Integer isPrimaryKey;
}
