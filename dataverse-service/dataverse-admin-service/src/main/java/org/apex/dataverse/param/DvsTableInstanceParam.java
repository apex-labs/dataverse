package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据表实例参数")
@Data
public class DvsTableInstanceParam {

    @ApiModelProperty("表实例ID")
    private Long tableInstanceId;

    @ApiModelProperty("表ID")
    private Long tableId;

    @ApiModelProperty("Storage Box ID")
    private Long storageBoxId;

    @ApiModelProperty("实例名称，命名规则可为table_name + env（BASIC，DEV，TEST，PROD）")
    private String instanceName;

    @ApiModelProperty("存储名称")
    private String storageName;

    @ApiModelProperty("存储类型,HDFS存储区，MySQL存储区，Oracle存储区，ClickHouse存储 区，Doris存储区")
    private Integer storageTypeId;

    @ApiModelProperty("表实例环境，1：BASIC、2:DEV、3：TEST、4：PROD")
    private Integer env;

    @ApiModelProperty("最近运行状态")
    private String lastRunStatus;
}
