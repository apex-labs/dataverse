package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据域请求参数")
@Data
public class DvsRegionParam {

    @ApiModelProperty("数据域ID")
    private Long DvsRegionId;

    @ApiModelProperty("所属数据空间ID")
    private Long DvsId;

    @ApiModelProperty("数据域名称，英文名称")
    private String regionName;

    @ApiModelProperty("数据域别名，显示名称")
    private String regionAlias;

    @ApiModelProperty("数据域简称，英文缩写")
    private String regionAbbr;

    @ApiModelProperty("所属数据仓库的分层，1:ODS，2:CDM，3:ADS")
    private Integer dwLayer;

    @ApiModelProperty("所属数据仓库的分层，ODS,DWD,DWS,DIM,DM,MASTER,MODEL")
    private String dwLayerDetail;

    @ApiModelProperty("数据域描述")
    private String description;
}
