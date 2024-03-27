package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据存储区请求参数")
@Data
public class DataStorageParam {

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty("存储区名称，英文名称")
    private String storageName;

    @ApiModelProperty("存储区别名，显示名称")
    private String storageAlias;

    @ApiModelProperty("存储区简称，英文缩写")
    private String storageAbbr;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private Integer storageTypeId;

    @ApiModelProperty("存储区链接方式，1：HDFS，2：JDBC链接")
    private Integer storageConnType;

    @ApiModelProperty("存储区描述信息")
    private String description;

    @ApiModelProperty("数据存储区配置参数")
    private DataStorageConfParam dataStorageConfParam;
}
