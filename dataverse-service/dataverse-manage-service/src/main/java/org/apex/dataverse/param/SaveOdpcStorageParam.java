package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: SaveOdpcStorageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/16 12:33
 */
@Data
@ApiModel
public class SaveOdpcStorageParam {

    @ApiModelProperty("主键")
    private Long odpcStorageId;

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private String storageType;

    @ApiModelProperty("Hadoop的NameNode节点, json array [{\"ip\":\"127.0.0.1\", \"host\":\"localhost\"}]")
    private String namenodes;

    @ApiModelProperty("Hadoop的resource managers, json array [{\"ip\":\"127.0.0.1\", \"host\":\"localhost\"}]")
    private String resourceManagers;

    @ApiModelProperty("Hadoop的datanodes, json array [{\"ip\":\"127.0.0.1\", \"host\":\"localhost\"}]")
    private String datanodes;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("存储区的版本号，如HDFS存储区的软件版本，如MySQL的JDBC存储区的版本")
    private String version;

    @ApiModelProperty("存储路径")
    private String storagePath;
}
