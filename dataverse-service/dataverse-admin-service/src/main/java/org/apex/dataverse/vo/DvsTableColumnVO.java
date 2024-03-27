package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 列
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Data
public class DvsTableColumnVO {

    @ApiModelProperty("列ID")
    private Long columnId;

    @ApiModelProperty("列编码，同一列在不同环境中编码相同")
    private String columnCode;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty("列名，英文，帕斯卡命名法则")
    private String columnName;

    @ApiModelProperty("列别名，显示名称")
    private String columnAlias;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("数据类型ID")
    private Integer dataTypeId;

    @ApiModelProperty("数据类型名称")
    private String dataTypeName;

    @ApiModelProperty("数据类型-短类型。1：字符串，2：整数，3：浮点，4：日期，5：日期时间")
    private Integer shortDataTypeId;

    @ApiModelProperty("是否主键, 1:是，0:否")
    private Integer isPrimaryKey;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
