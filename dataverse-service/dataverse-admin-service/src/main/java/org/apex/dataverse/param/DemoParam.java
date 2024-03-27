package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("请求demo参数")
@Data
public class DemoParam {

    @ApiModelProperty("dd")
    private String dd;
}
