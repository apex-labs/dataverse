package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 数据类型字典表，脚本初始化
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
public class DataTypeMapVO {

    @ApiModelProperty("数据类型ID")
    private Integer dataTypeId;

    @ApiModelProperty("数据类型名称")
    private String dataTypeName;

    @ApiModelProperty("数据类型别名")
    private String dataTypeAlias;

    @ApiModelProperty("数据类型-短类型。1：字符串，2：整数，3：浮点，4：日期，5：日期时间")
    private Integer shortDataTypeId;

    @ApiModelProperty("短数据类型别名")
    private String shortDataTypeAlias;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
