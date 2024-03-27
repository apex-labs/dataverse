package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("分页查询项目参数")
@Data
public class PageProjectParam {

    @ApiModelProperty("分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("项目名称")
    private String projectName;
}
