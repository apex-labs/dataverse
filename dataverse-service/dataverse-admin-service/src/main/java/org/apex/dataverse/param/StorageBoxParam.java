package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据箱参数")
@Data
public class StorageBoxParam {

    @ApiModelProperty("存储箱ID，主键")
    private Long storageBoxId;

    @ApiModelProperty("存储箱编码，不同环境中编码相同")
    private String storageBoxCode;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty(value = "存储区ID", required = true)
    private Long storageId;

    @ApiModelProperty("存储箱名称，命名规则为region_name + env (BASIC,DEV,TEST,PROD)")
    private String boxName;

    @ApiModelProperty("数据仓库分层，1：ODS，2：DW，3：ADS")
    private Integer dwLayer;

    @ApiModelProperty(value = "存储名称（库级别），如果存储在HDFS，则是一个目录的路径，存储在库中为库名", required = true)
    private String storageName;

    @ApiModelProperty(value = "存储类型，1：HDFS，2：MySQL，3：ClickHouse，4:Doris", required = true)
    private String storageType;

    @ApiModelProperty("数据存储箱环境，1：BASIC、2:DEV、3：PROD")
    private Integer env;

    @ApiModelProperty("存储格式，HDFS存储区时，存储的文件格式。如1：ORC，2：Parquet")
    private Integer storageFormat;

    @ApiModelProperty("当前存储箱中表的数量")
    private Integer tableCount;

    @ApiModelProperty("存储路径")
    private String storagePath;

}
