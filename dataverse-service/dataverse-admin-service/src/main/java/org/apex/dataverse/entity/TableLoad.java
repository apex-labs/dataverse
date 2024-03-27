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
 * 表输出
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TableLoad implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_load_id", type = IdType.AUTO)
    private Long tableLoadId;

    /**
     * ETL作业编码，同一作业在不同环境下etl_job_code相同
     */
    private String etlJobCode;

    /**
     * Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同
     */
    private String tableCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * Load至Dvs中的表名称
     */
    private String tableName;

    /**
     * 主键字段
     */
    private String pkField;

    /**
     * 主键字段说明
     */
    private String pkFieldComment;

    /**
     * 分区字段
     */
    private String partitionField;

    /**
     * 单分区抽取最多行数
     */
    private Integer partitionMaxRows;

    /**
     * 存储表名称，帕斯卡命名法则
     */
    private String storageName;

    /**
     * 存储表别名，中文名称
     */
    private String storageAlias;

    /**
     * 表备注说明
     */
    private String comment;

    /**
     * 定时规则，cron表达式
     */
    private String cron;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
