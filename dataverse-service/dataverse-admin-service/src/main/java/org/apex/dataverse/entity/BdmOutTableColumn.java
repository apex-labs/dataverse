package org.apex.dataverse.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BdmOutTableColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bdmOutColumnId;

    /**
     * 表ID
     */
    private Long tableId;

    /**
     * 列名，英文，帕斯卡命名法则
     */
    private String columnName;

    /**
     * 列别名，显示名称
     */
    private String columnAlias;

    /**
     * 数据类型ID
     */
    private Integer dataTypeId;

    /**
     * 数据类型名称
     */
    private String dataTypeName;

    /**
     * 数据类型-短类型。1：字符串，2：整数，3：浮点，4：日期，5：日期时间
     */
    private Integer shortDataTypeId;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 是否主键, 1:是，0:否
     */
    private Integer isPrimaryKey;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
