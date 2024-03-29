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

    @ApiModelProperty("数据空间特定环境下参数")
    private List<DvsEnvParam> dvsEnvParamList;
}
