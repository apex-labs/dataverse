package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ListDataRegionParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/1 9:27
 */
@Data
@ApiModel
public class ListDataRegionParam {

    @ApiModelProperty("数据空间编码")
    private String dvsCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("所属数据仓库的分层，1:ODS，2:DW，3:ADS")
    private Integer dwLayer;

    @ApiModelProperty("所属数据仓库的分层，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,")
    private String dwLayerDetail;
}
