package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: SaveJdbcStorageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/16 12:33
 */
@Data
@ApiModel
public class SaveJdbcStorageParam {

    @ApiModelProperty("主键")
    private Long jdbcStorageId;

    @ApiModelProperty("数据存储区ID")
    private Long storageId;

    @ApiModelProperty(value = "JDBC链接URL", required = true)
    private String jdbcUrl;

    @ApiModelProperty(value = "JDBC链接用户名", required = true)
    private String userName;

    @ApiModelProperty(value = "JDBC链接密码", required = true)
    private String password;

    @ApiModelProperty(value = "JDBC驱动类，如MySQL的com.mysql.jdbc.Driver", required = true)
    private String driverClass;

    @ApiModelProperty("链接配置JSON，如：{\"serverTimezone\":\"Asia/Shanghai\",\"useUnicode\":\"true\"}")
    private String connConfig;

    @ApiModelProperty(value = "存储区类型,1:HDFS,2:MYSQL,3:PGSQL,4:ORACLE,5:DORIS,6:STAR_ROCKS,7:CLICKHOUSE,8:ELASTICSEARCH", required = true)
    private String storageType;

    @ApiModelProperty("描述")
    private String description;
}
