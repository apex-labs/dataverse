package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
public class RegionMappingVO {

    @ApiModelProperty("业务域和数据域关联映射ID")
    private Long regionMappingId;

    @ApiModelProperty("业务域ID")
    private Long bizRegionId;

    @ApiModelProperty("数据域ID")
    private Long dataRegionId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
