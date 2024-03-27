package org.apex.dataverse.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class TableStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_storage_id", type = IdType.AUTO)
    private Long tableStorageId;

    private String tableCode;

    /**
     * 存储箱编码，不同环境中编码相同
     */
    private String boxCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 库名，一般同一个regin在同一个库中，既同region_name。
     */
    private String dbname;

    /**
     * 存储名称，存储在hdfs中，为存储路径，存储其它库中为表名
     */
    private String storageName;

    /**
     * 存储类型,HDFS存储区，MySQL存储区，Oracle存储区，ClickHouse存储 区，Doris存储区
     */
    private String storageType;

    /**
     * 数据文件格式化类型。JDBC库存储区时，为空
     * orc:https://orc.apache.org/docs/
     * parquet:https://parquet.apache.org/docs/
     * avro:https://avro.apache.org/docs/1.11.1/
     * delta:https://docs.delta.io/latest/index.html#
     * hudi:https://hudi.apache.org/docs/overview
     * iceberg:https://iceberg.apache.org/docs/latest/
     * paimon:https://paimon.apache.org
     */
    private String storageFormat;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最近更新时间
     */
    private LocalDateTime lastUpdateTime;


}
