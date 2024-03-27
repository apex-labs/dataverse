package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
public class DvsTableInstance {

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("最近运行状态")
    private String lastRunStatus;

    @ApiModelProperty("最近更新时间")
    private LocalDateTime lastUpdateTime;

}
