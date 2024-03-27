package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("分页查询数据域映射")
@Data
public class PageRegionMappingParam {

    @ApiModelProperty("数据域映射分页参数")
    private PageQueryParam pageQueryParam;

}
