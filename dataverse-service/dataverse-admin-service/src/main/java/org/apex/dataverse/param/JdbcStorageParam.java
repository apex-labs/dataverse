package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("Jdbc存储区配置参数")
@Data
public class JdbcStorageParam {

    @ApiModelProperty("Jdbc存储区配置主键")
    private Long jdbcStorageId;

    @ApiModelProperty("数据存储区ID")
    private Long storageId;

    @ApiModelProperty("JDBC链接URL")
    private String jdbcUrl;

    @ApiModelProperty("JDBC链接用户名")
    private String userName;

    @ApiModelProperty("JDBC链接密码")
    private String password;

    @ApiModelProperty("链接配置JSON，如：{\"serverTimezone\":\"Asia/Shanghai\",\"useUnicode\":\"true\"}")
    private String connConfig;

    @ApiModelProperty("存储区类型，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private Integer storageType;

    @ApiModelProperty("描述")
    private String description;
}
