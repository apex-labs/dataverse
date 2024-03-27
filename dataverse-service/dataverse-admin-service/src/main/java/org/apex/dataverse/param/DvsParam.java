package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("数据空间参数")
@Data
public class DvsParam {

    @ApiModelProperty("数据空间ID")
    private Long dvsId;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据空间名称，英文名称")
    private String dvsName;

    @ApiModelProperty("数据空间别名，中文/显示名称")
    private String dvsAlias;

    @ApiModelProperty("数据空间缩写，英文简称")
    private String dvsAbbr;

    @ApiModelProperty("环境模式，1：BASIC、2：DEV、3：TEST、4：PROD")
    private Integer env;

    @ApiModelProperty("数据仓库状态，1：规划中，2：开发中")
    private Integer dvsStatus;

    @ApiModelProperty("0：未完成，1：层之间无隔离（三层同一存储区），2：ODS-CDM无隔离，3：全隔离")
    private Integer layerAtaIsolation;

    @ApiModelProperty("数据空间描述")
    private String description;

    @ApiModelProperty("是否多存储区，0：单存储区，1：多存储区")
    private Integer multStorage;

//    @ApiModelProperty("存储桶参数 3种数据数据仓库分层")
//    List<StorageBoxParam> storageBoxParamList;
}
