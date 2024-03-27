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
public class RegionMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务域和数据域关联映射ID
     */
    @TableId(value = "region_mapping_id", type = IdType.AUTO)
    private Long regionMappingId;

    /**
     * 业务域ID
     */
    private Long bizRegionId;

    /**
     * 数据域ID
     */
    private Long dataRegionId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
