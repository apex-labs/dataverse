package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("分页查询数据空间参数")
@Data
public class PageDvsParam {

    @ApiModelProperty("数据空间分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据空间查询关键字 数据空间ID/数据空间名称/别名/简称 数据空间ID精确查询")
    private String keyword;
}
