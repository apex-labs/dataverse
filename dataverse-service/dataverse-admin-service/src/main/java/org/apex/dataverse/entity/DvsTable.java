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
 * 表
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DvsTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    @TableId(value = "table_id", type = IdType.AUTO)
    private Long tableId;

    /**
     * 表编码。同一Evn下，表编码唯一。table_code+env唯一
     */
    private String tableCode;

    /**
     * 数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同
     */
    private String dvsCode;

    /**
     * 数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域
     */
    private String dataRegionCode;

    /**
     * 表名，英文名称
     */
    private String tableName;

    /**
     * 表别名，显示名称
     */
    private String tableAlias;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 数仓分层，1：ODS，2：CDM，3：ADS
     */
    private Integer dwLayer;

    /**
     * 所属数据仓库的分层，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,
     */
    private Integer dwLayerDetail;

    /**
     * 是否流式表，1：是，0：否
     */
    private Integer isStream;

    /**
     * 建表模式，1：系统自动创建，2：手动创建
     */
    private Integer createMode;

    /**
     * 表的生命周期/状态
     * 开发中（草稿）：11
     * 开发完成：12
     * 测试中：21
     * 测试完成(通过）：22
     * 调度编排中：31
     * 调度编排完成：32
     * 调度测试中：41
     * 调度测试完成(通过）：42
     * 已发布生产：51
     * 生产测试中：61
     * 生产测试完成(通过）：62
     * 已上线：71
     * 已下线：72
     */
    private Integer tableLifecycle;

    /**
     * 是否删除：0否，1：是
     */
    private Integer isDeleted;

    /**
     * 表描述信息
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
     * 创建人所属租户ID
     */
    private Long tenantId;

    /**
     * 创建人所属租户名称【冗余】
     */
    private String tenantName;

    /**
     * 创建人所属部门ID
     */
    private Long deptId;

    /**
     * 创建人所属部门名称【冗余】
     */
    private String deptName;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;


}
