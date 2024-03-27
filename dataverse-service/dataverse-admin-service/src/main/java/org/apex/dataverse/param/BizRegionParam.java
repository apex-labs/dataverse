package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("业务域参数")
@Data
public class BizRegionParam {

    @ApiModelProperty("业务域ID")
    private Long bizRegionId;

    @ApiModelProperty("业务域名称")
    private String bizRegionName;

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("业务域描述信息")
    private String description;
}
