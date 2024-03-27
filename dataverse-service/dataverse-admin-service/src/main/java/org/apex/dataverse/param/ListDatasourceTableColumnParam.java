package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ListDatasourceTableColumnParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/6/1 17:34
 */
@Data
public class ListDatasourceTableColumnParam extends ListDatasourceTableParam {

    @ApiModelProperty("表名称")
    private String tableName;

}
