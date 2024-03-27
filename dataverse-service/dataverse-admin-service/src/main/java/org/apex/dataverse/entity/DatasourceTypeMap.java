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
public class DatasourceTypeMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源类型ID
     */
    private Integer datasourceTypeId;

    /**
     * 数据源类型名称，如MySQL、Oracle、PostgreSQL等
     */
    private String datasourceTypeName;

    /**
     * 数据源类型图标
     */
    private String datasourceIcon;

    /**
     * 数据源类型分组ID。1、关系型数据库，2：NoSQL数据库，3：消息中间件
     */
    private Integer datasourceCategoryId;

    /**
     * 数据源类型分组名称。关系型数据库，NoSQL数据库，消息中间件
     */
    private String datasourceCategoryName;

    /**
     * 是否生效，0无效，1有效
     */
    private Integer isValid;

    /**
     * 备注说明
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
