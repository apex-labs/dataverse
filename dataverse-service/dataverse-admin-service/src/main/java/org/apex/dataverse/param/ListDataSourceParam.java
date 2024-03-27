package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ListDataSourceParam
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 09:51
 **/
@Data
public class ListDataSourceParam {

    @ApiModelProperty(value = "数据源类型ID", required = true)
    private Integer datasourceTypeId;

}
