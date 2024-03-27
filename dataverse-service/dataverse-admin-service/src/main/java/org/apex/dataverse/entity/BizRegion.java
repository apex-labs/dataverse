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
public class BizRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务域ID
     */
    @TableId(value = "biz_region_id", type = IdType.AUTO)
    private Long bizRegionId;

    /**
     * 业务域名称
     */
    private String bizRegionName;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 业务域描述信息
     */
    private String description;

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


}
