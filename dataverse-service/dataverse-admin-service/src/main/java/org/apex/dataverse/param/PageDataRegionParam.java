package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageDataRegionParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/17 16:07
 */
@Data
public class PageDataRegionParam {

    @ApiModelProperty("数据域分页查询参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("数据空间编码")
    private String dvsCode;

    @ApiModelProperty("作业类型 1:集成作业, 2:开发作业, 为空查询空间下所有的数据域")
    private Integer jobType;

    @ApiModelProperty("数据域分层明细")
    private Integer dwLayerDetail;

    @ApiModelProperty("搜索关键词")
    private String keyword;
}
