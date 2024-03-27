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
 *
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存储区ID
     */
    @TableId(value = "storage_id", type = IdType.AUTO)
    private Long storageId;

    /**
     * 存储区名称，英文名称，唯一
     */
    private String storageName;

    /**
     * 存储区别名，显示名称
     */
    private String storageAlias;

    /**
     * 存储区简称，英文缩写
     */
    private String storageAbbr;

    /**
     * 存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle
     */
    private Integer storageTypeId;

    /**
     * 存储区链接方式，1：HDFS，2：JDBC链接
     */
    private Integer storageConnType;

    /**
     * 存储区描述信息
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人所属租户ID
     */
    private Long tenantId;

    /**
     * 创建人所属租户名称【冗余】
     */
    private String tenantName;

    /**
     * 创建人所属部门ID
     */
    private Long deptId;

    /**
     * 创建人所属部门名称【冗余】
     */
    private String deptName;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;


}
