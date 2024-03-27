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
public class TableStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_statistics_id", type = IdType.AUTO)
    private Long tableStatisticsId;

    private String tableCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 表中记录数，行数
     */
    private Integer count;

    /**
     * 表所占空间大小
     */
    private Double size;

    /**
     * 文件个数
     */
    private Integer fileCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
