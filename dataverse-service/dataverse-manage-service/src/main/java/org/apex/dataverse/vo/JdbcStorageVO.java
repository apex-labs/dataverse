package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: JdbcStorageVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/16 11:00
 */
@ApiModel
@Data
public class JdbcStorageVO {

    @ApiModelProperty("主键")
    private Long jdbcStorageId;

    @ApiModelProperty("数据存储区ID")
    private Long storageId;

    @ApiModelProperty("JDBC链接URL")
    private String jdbcUrl;

    @ApiModelProperty("JDBC链接用户名")
    private String userName;

    @ApiModelProperty("JDBC链接密码")
    private String password;

    @ApiModelProperty("JDBC驱动类，如MySQL的com.mysql.jdbc.Driver")
    private String driverClass;

    @ApiModelProperty("链接配置JSON，如：{\"serverTimezone\":\"Asia/Shanghai\",\"useUnicode\":\"true\"}")
    private String connConfig;

    @ApiModelProperty("存储区类型,1:HDFS,2:MYSQL,3:PGSQL,4:ORACLE,5:DORIS,6:STAR_ROCKS,7:CLICKHOUSE,8:ELASTICSEARCH")
    private String storageType;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
