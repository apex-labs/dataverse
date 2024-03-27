package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DvsAdsTableParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/22 9:31
 */
@Data
public class DvsAdsTableParam {

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("数据空间编码集合")
    private List<String> dvsCodeList;

    @ApiModelProperty("环境模式, 1:DEV, 2:PROD")
    private Integer env;
}
