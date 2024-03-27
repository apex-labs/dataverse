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
public class StorageBox implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存储箱ID，主键
     */
    @TableId(value = "box_id", type = IdType.AUTO)
    private Long boxId;

    /**
     * 存储箱编码，不同环境中编码相同
     */
    private String boxCode;

    /**
     * 数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同
     */
    private String dvsCode;

    /**
     * 存储区ID
     */
    private Long storageId;

    /**
     * 存储箱名称，命名规则为region_name + env (BASIC,DEV,TEST,PROD)
     */
    private String boxName;

    /**
     * 数据仓库分层，1：ODS，21：DW，31：ADS
     */
    private Integer dwLayer;

    /**
     * 存储名称（库级别），如果存储在HDFS，则是一个目录的路径，存储在库中为库名
     */
    private String storageName;

    /**
     * 存储类型，1：HDFS，2：MySQL，3：ClickHouse，4:Doris
     */
    private String storageType;

    /**
     * 数据存储箱环境，1：BASIC、2:DEV、3：TEST、4：PROD
     */
    private Integer env;

    /**
     * 存储格式，HDFS存储区时，存储的文件格式。如1：ORC，2：Parquet
     */
    private Integer storageFormat;

    /**
     * 当前存储箱中表的数量
     */
    private Integer tableCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
