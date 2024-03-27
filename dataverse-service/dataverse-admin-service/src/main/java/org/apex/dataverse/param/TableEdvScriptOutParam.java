package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("加入开发任务")
public class TableEdvScriptOutParam {

    @ApiModelProperty("数据处理SQL")
    private String edvSql;

    @ApiModelProperty("引擎类型，1:spark引擎，2：flink引擎")
    private Integer engineType;

    @ApiModelProperty("输出域")
    private Long DvsRegionId;

    @ApiModelProperty("表输出类型，1：临时表，2：正式表-逻辑，3：正式表-物理")
    private Integer edvOutType;

    @ApiModelProperty("表名，英文名，帕斯卡命名法则")
    private String tableName;

    @ApiModelProperty("表别名")
    private String tableAlias;
    
    @ApiModelProperty("来自所选择的region中，所属数据仓库的分层，1:ODS，2:CDM，3:ADS")
    private Integer dwLayer;

    @ApiModelProperty("来自所选择的region中，所属数据仓库的分层，ODS,DWD,DWS,DIM")
    private String dwLayerDetail;

    @ApiModelProperty("创建人所属租户ID")
    private Long tenantId;

    @ApiModelProperty("创建人所属租户名称")
    private String tenantName;

    @ApiModelProperty("创建人所属部门ID")
    private Long deptId;

    @ApiModelProperty("创建人所属部门名称")
    private String deptName;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称")
    private String userName;

}
