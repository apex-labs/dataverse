package org.apex.dataverse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 列
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DvsTableColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 列ID
     */
    @TableId(value = "column_id", type = IdType.AUTO)
    private Long columnId;

    /**
     * 列编码，同一列在不同环境中编码相同
     */
    private String columnCode;

    private String tableCode;

    /**
     * 列名，英文，帕斯卡命名法则
     */
    private String columnName;

    /**
     * 列别名，显示名称
     */
    private String columnAlias;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

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
