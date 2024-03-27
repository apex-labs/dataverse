package org.apex.dataverse.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: StorageVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/12 11:36
 */
@ApiModel
@Data
public class StorageVO {

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty("存储区名称，英文名称，唯一")
    private String storageName;

    @ApiModelProperty("存储区别名，显示名称")
    private String storageAlias;

    @ApiModelProperty("存储区简称，英文缩写")
    private String storageAbbr;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private String storageType;

    @ApiModelProperty("引擎类型，1：Spark, 2: flink，3：非自研引擎（如doris, mysql)")
    private String engineType;

    @ApiModelProperty("存储区链接方式，1：ODPC，2：JDBC链接")
    private String connType;

    @ApiModelProperty("存储区描述信息")
    private String description;

    @ApiModelProperty("创建人ID")
    private Long creatorId;

    @ApiModelProperty("创建人姓名【冗余】")
    private String creatorName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("JDBC存储区")
    private JdbcStorageVO jdbcStorageVO;

    @ApiModelProperty("ODPC存储区")
    private OdpcStorageVO odpcStorageVO;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;
}
