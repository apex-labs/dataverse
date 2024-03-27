package org.apex.dataverse.port.entity;

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
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Storage implements Serializable {

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
    private String storageType;

    /**
     * 引擎类型，1：Spark，2：Flink，3：非自研引擎（如doris, mysql)
     */
    private String engineType;

    /**
     * 存储区链接方式，1：ODPC，2：JDBC链接
     */
    private String connType;

    /**
     * 存储区描述信息
     */
    private String description;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名【冗余】
     */
    private String creatorName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
