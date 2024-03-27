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
 *
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EtlJobGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "etl_group_id", type = IdType.AUTO)
    private Long etlGroupId;

    /**
     * Etl作业分组名称
     */
    private String etlGroupName;

    /**
     * Etl作业分组编码
     */
    private String etlGroupCode;

    /**
     * 数据域编码
     */
    private String dataRegionCode;

    /**
     * Etl作业分组父编码
     */
    private String parentEtlGroupCode;

    /**
     * 分组层级
     */
    private Integer groupLevel;

    /**
     * 数据源类型ID
     */
    private Integer datasourceTypeId;

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
