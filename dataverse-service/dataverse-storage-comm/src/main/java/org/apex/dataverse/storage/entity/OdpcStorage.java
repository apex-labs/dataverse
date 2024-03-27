package org.apex.dataverse.storage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自定义ODPC存储区（用自行开发的Spark/Flink Engine)，如基于HDFS的存储区+SparkEngine
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OdpcStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "odpc_storage_id", type = IdType.AUTO)
    private Long odpcStorageId;

    /**
     * 存储区ID
     */
    private Long storageId;

    /**
     * 存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle
     */
    private String storageType;

    /**
     * Hadoop的NameNode节点, json array [{"ip":"127.0.0.1", "host":"localhost"}]
     */
    private String namenodes;

    /**
     * Hadoop的resource managers, json array [{"ip":"127.0.0.1", "host":"localhost"}]
     */
    private String resourceManagers;

    /**
     * Hadoop的datanodes, json array [{"ip":"127.0.0.1", "host":"localhost"}]
     */
    private String datanodes;

    /**
     * 描述
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
     * 存储区的版本号，如HDFS存储区的软件版本，如MySQL的JDBC存储区的版本
     */
    private String version;

    /**
     * 存储路径
     */
    private String storagePath;


}
