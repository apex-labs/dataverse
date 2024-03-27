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
 * 表输入
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TableExtract implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_extract_id", type = IdType.AUTO)
    private Long tableExtractId;

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
     * 源表名，数据源中的表
     */
    private String originTableName;

    /**
     * 源表备注，数据源表中的备注
     */
    private String originTableComment;

    /**
     * 增量类型。0：非增量抽取，1：日期增量，2：数值增量
     */
    private Integer incrType;

    /**
     * 增量标记字段，日期增量可多个字段，多个字段之间逗号分隔，数值增量支持单个字段。
     */
    private String incrFields;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
