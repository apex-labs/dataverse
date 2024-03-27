package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ListDatasourceTableParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/6/1 17:25
 */
@Data
public class ListDatasourceTableParam {

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

    @ApiModelProperty("环境模式, 0:BASIC,1:DEV,2:PROD")
    private Integer env;
}
