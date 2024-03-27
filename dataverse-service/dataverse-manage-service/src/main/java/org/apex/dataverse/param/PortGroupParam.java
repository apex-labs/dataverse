package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: PortGroupParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 16:50
 */
@Data
@ApiModel
public class PortGroupParam {

    @ApiModelProperty("组ID")
    private Long groupId;

    @ApiModelProperty("组编码，唯一")
    private String groupCode;

    @ApiModelProperty("组名")
    private String groupName;
}
