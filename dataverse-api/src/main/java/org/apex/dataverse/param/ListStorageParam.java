package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ListStorageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/15 16:33
 */
@Data
@ApiModel
public class ListStorageParam {

    @ApiModelProperty("存储区ID集合")
    private List<Long> storageIds;

    @ApiModelProperty("存储区名称")
    private String storageName;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private String storageType;

    @ApiModelProperty("存储区链接方式，1：ODPC，2：JDBC链接")
    private String connType;
}
