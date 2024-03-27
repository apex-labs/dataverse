package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("数据域分页参数")
@Data
public class PageDvsRegionParam {

    @ApiModelProperty("数据域分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据域ID")
    private Integer DvsId;

    @ApiModelProperty("所属数据仓库的分层，1:ODS，2:CDM，3:ADS")
    private Integer dwLayer;

    @ApiModelProperty("关键字查询 数据域ID/数据域名称/数据域别名/数据域简称")
    private String keyword;
}
