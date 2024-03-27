package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: EtlJobGroupTreeParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/1 10:33
 */
@Data
@ApiModel
public class EtlJobGroupTreeParam {

    @ApiModelProperty("数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域")
    private String dataRegionCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;
}
