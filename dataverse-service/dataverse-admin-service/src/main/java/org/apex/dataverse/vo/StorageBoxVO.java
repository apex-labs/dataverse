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
 * @since 2023-05-15
 */
@Data
public class StorageBoxVO {

    @ApiModelProperty("存储箱ID，主键")
    private Long storageBoxId;

    @ApiModelProperty("存储箱编码，不同环境中编码相同")
    private String storageBoxCode;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty("存储箱名称，命名规则为region_name + env (BASIC,DEV,TEST,PROD)")
    private String boxName;

    @ApiModelProperty("数据仓库分层，1：ODS，2：DW，3：ADS")
    private Integer dwLayer;

    @ApiModelProperty("存储名称（库级别），如果存储在HDFS，则是一个目录的路径，存储在库中为库名")
    private String storageName;

    @ApiModelProperty("存储类型，1：HDFS，2：MySQL，3：ClickHouse，4:Doris")
    private String storageType;

    @ApiModelProperty("数据存储箱环境，1：BASIC、2:DEV、3：TEST、4：PROD")
    private Integer env;

    @ApiModelProperty("存储格式，HDFS存储区时，存储的文件格式。如1：ORC，2：Parquet")
    private String storageFormat;

    @ApiModelProperty("当前存储箱中表的数量")
    private Integer tableCount;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
