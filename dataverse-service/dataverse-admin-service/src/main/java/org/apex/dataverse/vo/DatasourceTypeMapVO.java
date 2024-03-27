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
 * @since 2023-02-17
 */
@Data
public class DatasourceTypeMapVO {

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

    @ApiModelProperty("数据源类型名称，如MySQL、Oracle、PostgreSQL等")
    private String datasourceTypeName;

    @ApiModelProperty("数据源类型图标")
    private String datasourceIcon;

    @ApiModelProperty("数据源类型分组ID。1、关系型数据库，2：NoSQL数据库，3：消息中间件")
    private Integer datasourceCategoryId;

    @ApiModelProperty("数据源类型分组名称。关系型数据库，NoSQL数据库，消息中间件")
    private String datasourceCategoryName;

    @ApiModelProperty("是否生效，0无效，1有效")
    private Integer isValid;

    @ApiModelProperty("备注说明")
    private String comment;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
