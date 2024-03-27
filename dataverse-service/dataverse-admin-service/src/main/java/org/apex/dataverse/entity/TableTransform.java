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
 * 转换
 * </p>
 *
 * @author danny
 * @since 2024-01-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TableTransform implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_transform_id", type = IdType.AUTO)
    private Long tableTransformId;

    /**
     * ETL作业编码，同一作业在不同环境下etl_job_code相同
     */
    private String etlJobCode;

    /**
     * Load至Datavs中的表code，同一表在不同环境（dev/prod)下相同
     */
    private String tableCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 输入列名
     */
    private String inColumn;

    /**
     * 转换
     */
    private String transform;

    /**
     * 输出列名
     */
    private String outColumn;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 源字段类型ID
     */
    private Integer inDataTypeId;

    /**
     * 源字段类型名称
     */
    private String inDataTypeName;

    /**
     * 输出字段类型ID
     */
    private Integer outDataTypeId;

    /**
     * 输出字段类型名称
     */
    private String outDataTypeName;


}
