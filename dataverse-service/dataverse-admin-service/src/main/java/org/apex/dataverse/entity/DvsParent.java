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
public class DvsParent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据空间父ID
     */
    @TableId(value = "parent_id", type = IdType.AUTO)
    private Long parentId;

    /**
     * Dvs父编码，同parent下Dvs在多个环境(dev/prod)下Dvs_code相同。
     */
    private String dvsCode;

    /**
     * 数据空间名称，英文名称
     */
    private String parentName;

    /**
     * 数据空间别名，中文/显示名称
     */
    private String parentAlias;

    /**
     * 数据空间缩写，英文简称
     */
    private String parentAbbr;

    /**
     * 环境模式，1：BASIC、2：DEV-PROD
     */
    private Integer envMode;

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
