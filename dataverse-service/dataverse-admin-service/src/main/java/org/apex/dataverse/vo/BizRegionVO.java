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
 * @since 2023-05-15
 */
@Data
public class BizRegionVO {

    @ApiModelProperty("业务域ID")
    private Long bizRegionId;

    @ApiModelProperty("业务域名称")
    private String bizRegionName;

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("业务域描述信息")
    private String description;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
