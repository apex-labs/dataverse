package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DwLayerVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/17 15:58
 */
@Data
public class DwLayerVO {

    @ApiModelProperty("数据分层对应数字")
    private Integer value;

    @ApiModelProperty("数据分层对应的字符")
    private String desc;
}
