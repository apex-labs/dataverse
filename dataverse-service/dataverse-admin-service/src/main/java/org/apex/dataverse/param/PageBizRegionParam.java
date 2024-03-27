package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("分页查询业务域参数")
@Data
public class PageBizRegionParam {

    @ApiModelProperty("业务域分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("业务域名称")
    private String bizRegionName;
}
