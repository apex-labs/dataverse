package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("存储类型请求参数")
@Data
public class StorageTypeMapParam {

    @ApiModelProperty("存储区类型名称，HDFS存储区，MySQL存储区，Oracle存储区，ClickHouse存储 区，Doris存储区")
    private String storageTypeName;

    @ApiModelProperty("存储区链接类型，1：HDFS，2：JDBC")
    private Integer storageConnType;

}
