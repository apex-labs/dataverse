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
 * 表数据处理输入
 * </p>
 *
 * @author danny
 * @since 2024-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BdmInTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "bdm_in_table_id", type = IdType.AUTO)
    private Long bdmInTableId;

    private String bdmJobCode;

    /**
     * 开发任务依赖表编码
     */
    private String tableCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 所属数据域
     */
    private String dataRegionCode;

    /**
     * 表名，英文名，帕斯卡命名法则
     */
    private String tableName;

    /**
     * 表别名
     */
    private String tableAlias;

    /**
     * 描述
     */
    private String description;

    /**
     * 来自所选择的region中，所属数据仓库的分层，1:ODS，2:CDM，3:ADS
     */
    private Integer dwLayer;

    /**
     * 所属数据仓库的分层，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,
     */
    private String dwLayerDetail;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
