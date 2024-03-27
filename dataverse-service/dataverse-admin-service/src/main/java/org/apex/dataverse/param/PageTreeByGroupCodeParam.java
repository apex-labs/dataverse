package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageTreeByGroupCodeParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 17:40
 */
@Data
public class PageTreeByGroupCodeParam {

    @ApiModelProperty("数据集成分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据分组CODE")
    private String groupCode;

    @ApiModelProperty("数据源类型")
    private Integer datasourceTypeId;
}
