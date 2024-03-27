package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: EnumInfoVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/12 13:23
 */
@Data
@ApiModel
public class EnumInfoVO {

    @ApiModelProperty("枚举值")
    private Integer value;

    @ApiModelProperty("枚举值描述")
    private String desc;
}
