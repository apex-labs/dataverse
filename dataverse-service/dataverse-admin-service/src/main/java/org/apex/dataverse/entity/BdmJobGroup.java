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
 * 大数据建模作业分组
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BdmJobGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "bdm_job_group_id", type = IdType.AUTO)
    private Integer bdmJobGroupId;

    /**
     * 数据域编码
     */
    private String dataRegionCode;

    /**
     * 大数据建模作业分组
     */
    private String bdmGroupName;

    /**
     * 大数据建模作业分组编码
     */
    private String bdmGroupCode;

    /**
     * 大数据建模作业分组父编码
     */
    private String parentBdmGroupCode;

    /**
     * 大数据建模作业分组层级
     */
    private Integer groupLevel;

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
