package org.apex.dataverse.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: StoragePortVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 15:57
 */
@ApiModel
@Data
public class StoragePortVO {

    @ApiModelProperty("storagePort对应的主键")
    private Long storagePortId;

    @ApiModelProperty("storage_id")
    private Long storageId;

    @ApiModelProperty("Storage's connection type JDBC,ODPC")
    private String connType;

    @ApiModelProperty("Engine type，1：Spark，2：Flink，3：非自研引擎（如doris, mysql)")
    private String engineType;

    @ApiModelProperty("port_id")
    private Long portId;

    @ApiModelProperty("port_code")
    private String portCode;

    @ApiModelProperty("port_name")
    private String portName;

    @ApiModelProperty("Port和JDBC存储区间允许创建的最小链接数。")
    private Integer minJdbcConns;

    @ApiModelProperty("Port和JDBC存储区间允许创建的最大链接数。")
    private Integer maxJdbcConns;

    @ApiModelProperty("ODPC存储区允许创建的最小引擎数")
    private Integer minOdpcEngines;

    @ApiModelProperty("ODPC存储区允许创建的最大引擎数")
    private Integer maxOdpcEngines;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
