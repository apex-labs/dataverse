package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageDeployParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/31 17:20
 */
@Data
public class PageDeployParam {

    @ApiModelProperty("分页查询参数")
    private PageQueryParam pageQueryParam;
}
