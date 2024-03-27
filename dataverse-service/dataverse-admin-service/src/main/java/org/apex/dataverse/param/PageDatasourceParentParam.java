package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("数据源列表请求参数")
@Data
public class PageDatasourceParentParam {

    @ApiModelProperty("分页查询参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据源类型 例如:Mysql/Oracle")
    private Integer dataSourceTypeId;

    @ApiModelProperty("数据源读写权限，1：只读，2：读写")
    private Integer dataSourceReadAndWrite;

    @ApiModelProperty("数据源查询关键字 数据源ID/数据源名称/简称")
    private String keyword;

}
