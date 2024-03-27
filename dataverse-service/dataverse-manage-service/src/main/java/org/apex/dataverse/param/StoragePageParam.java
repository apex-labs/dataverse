package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: StoragePageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/12 11:37
 */
@Data
@ApiModel
public class StoragePageParam {

    @ApiModelProperty("分页查询参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("存储区名称")
    private String storageName;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private String storageType;

    @ApiModelProperty("存储区链接方式，1：ODPC，2：JDBC链接")
    private String connType;

    @ApiModelProperty("引擎类型，1：Spark, 2: flink，3：非自研引擎（如doris, mysql)")
    private String engineType;
}
