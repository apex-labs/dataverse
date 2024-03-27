package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据源地图请求参数")
@Data
public class DatasourceTypeMapParam {

    @ApiModelProperty("数据源类型名称，如MySQL、Oracle、PostgreSQL等")
    private String datasourceTypeName;

    @ApiModelProperty("数据地图类型 1:关系型 2:NOSQL 3:消息中间件")
    private Integer datasourceType;
}
