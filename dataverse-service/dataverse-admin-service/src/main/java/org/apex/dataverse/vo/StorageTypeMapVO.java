package org.apex.dataverse.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorageTypeMapVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存储区类型ID
     */
    @TableId(value = "storage_type_id", type = IdType.AUTO)
    private Integer storageTypeId;

    /**
     * 存储区类型名称，HDFS存储区，MySQL存储区，Oracle存储区，ClickHouse存储 区，Doris存储区
     */
    private String storageTypeName;

    /**
     * 存储区链接类型，1：HDFS，2：JDBC
     */
    private Integer storageConnType;

    /**
     * 即席查询。0：否，1：是
     */
    private Integer adhocQuery;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
