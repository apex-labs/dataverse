package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: TableStorageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/23 18:58
 */
@Data
public class TableStorageParam {

    @ApiModelProperty("tableStorageId")
    private Long tableStorageId;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty("存储箱编码，不同环境中编码相同")
    private String storageBoxCode;

    @ApiModelProperty("存储名称，存储在hdfs中，为存储路径，存储其它库中为表名")
    private String storageName;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("库名，一般同一个regin在同一个库中，既同region_name。")
    private String dbname;

    @ApiModelProperty("存储类型,HDFS存储区，MySQL存储区，Oracle存储区，ClickHouse存储 区，Doris存储区")
    private Integer storageTypeId;

    @ApiModelProperty("数据文件格式化类型。JDBC库存储区时，为空\norc:https://orc.apache.org/docs/\nparquet:https://parquet.apache.org/docs/\navro:https://avro.apache.org/docs/1.11.1/\ndelta:https://docs.delta.io/latest/index.html#\nhudi:https://hudi.apache.org/docs/overview\niceberg:https://iceberg.apache.org/docs/latest/")
    private String storageFormat;

}
