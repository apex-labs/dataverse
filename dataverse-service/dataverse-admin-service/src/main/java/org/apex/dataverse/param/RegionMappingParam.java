package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("业务域和数据域参数")
@Data
public class RegionMappingParam {

    @ApiModelProperty("业务域和数据域关联映射ID")
    private Long regionMappingId;

    @ApiModelProperty("业务域ID")
    private Long bizRegionId;

    @ApiModelProperty("数据域ID")
    private Long dataRegionId;
}
