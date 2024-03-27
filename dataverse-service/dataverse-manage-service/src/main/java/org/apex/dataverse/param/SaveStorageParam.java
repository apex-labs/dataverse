package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.vo.JdbcStorageVO;
import org.apex.dataverse.vo.OdpcStorageVO;

import java.time.LocalDateTime;

/**
 * @ClassName: SaveStorageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/12 11:54
 */
@Data
@ApiModel
public class SaveStorageParam {

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty(value = "存储区名称，英文名称，唯一", required = true)
    private String storageName;

    @ApiModelProperty(value = "存储区别名，显示名称", required = true)
    private String storageAlias;

    @ApiModelProperty(value = "存储区简称，英文缩写", required = true)
    private String storageAbbr;

    @ApiModelProperty(value = "存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle", required = true)
    private String storageType;

    @ApiModelProperty(value = "引擎类型，1：Spark, 2: flink，3：非自研引擎（如doris, mysql)", required = true)
    private String engineType;

    @ApiModelProperty(value = "存储区链接方式，1：ODPC，2：JDBC链接", required = true)
    private String connType;

    @ApiModelProperty("存储区描述信息")
    private String description;

    @ApiModelProperty("JDBC存储区保存参数")
    private SaveJdbcStorageParam saveJdbcStorageParam;

    @ApiModelProperty("ODPC存储区保存参数")
    private SaveOdpcStorageParam saveOdpcStorageParam;

}
