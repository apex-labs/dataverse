package org.apex.dataverse.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据类型字典表，脚本初始化
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataTypeMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据类型ID
     */
    private Integer dataTypeId;

    /**
     * 数据类型名称
     */
    private String dataTypeName;

    /**
     * 数据类型别名
     */
    private String dataTypeAlias;

    /**
     * 数据类型-短类型。1：字符串，2：整数，3：浮点，4：日期，5：日期时间
     */
    private Integer shortDataTypeId;

    /**
     * 短数据类型别名
     */
    private String shortDataTypeAlias;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
