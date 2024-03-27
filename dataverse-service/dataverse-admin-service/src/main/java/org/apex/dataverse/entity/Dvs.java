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
public class
Dvs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据空间ID
     */
    @TableId(value = "dvs_id", type = IdType.AUTO)
    private Long dvsId;

    /**
     * 数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同
     */
    private String dvsCode;

    /**
     * 数据空间名称，英文名称
     */
    private String dvsName;

    /**
     * 数据空间别名，中文/显示名称
     */
    private String dvsAlias;

    /**
     * 数据空间缩写，英文简称
     */
    private String dvsAbbr;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 数据仓库状态，1：规划中（草稿），2：开发中，3：已上线
     */
    private Integer dvsStatus;

    /**
     * 0：未完成，1：层之间无隔离（三层同一存储区），2：ODS-CDM无隔离，3：全隔离
     */
    private Integer layerAtaIsolation;

    /**
     * 数据空间描述
     */
    private String description;

    /**
     * 是否多存储区，0：单存储区，1：多存储区
     */
    private Integer multStorage;

    /**
     * 是否删除：0否，1：是
     */
    private Integer isDeleted;

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
