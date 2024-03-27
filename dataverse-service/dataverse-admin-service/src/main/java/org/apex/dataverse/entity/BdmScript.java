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
 * 表数据处理信息，处理脚本信息
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BdmScript implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据建模任务ID
     */
    @TableId(value = "bdm_script_id", type = IdType.AUTO)
    private Long bdmScriptId;

    /**
     * 大数据建模作业编码
     */
    private String bdmJobCode;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 数据建模脚本
     */
    private String bdmScript;

    /**
     * 引擎类型，1:spark引擎，2：flink引擎
     */
    private Integer engineType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
