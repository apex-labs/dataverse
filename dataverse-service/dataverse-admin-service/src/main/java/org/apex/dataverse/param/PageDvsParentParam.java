package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageDvsParentParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/17 14:29
 */
@Data
public class PageDvsParentParam {

    @ApiModelProperty("数据空间分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据空间查询关键字 数据空间ID/数据空间名称/别名/简称 数据空间ID精确查询")
    private String keyword;
}
