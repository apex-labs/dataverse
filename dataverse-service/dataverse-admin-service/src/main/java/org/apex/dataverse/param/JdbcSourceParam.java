package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("JDBC实例请求参数")
@Data
public class JdbcSourceParam {

    @ApiModelProperty("JDBC实例ID")
    private Long jdbcSourceId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty(value = "环境模式, 1:BASIC,2:DEV,3:TEST,4:PROD", required = true)
    private Integer env;

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String datasourceName;

    @ApiModelProperty("JDBC链接URL")
    private String jdbcUrl;

    @ApiModelProperty("JDBC链接用户名")
    private String userName;

    @ApiModelProperty("JDBC链接密码")
    private String password;

    @ApiModelProperty("链接配置JSON，如：{\"serverTimezone\":\"Asia/Shanghai\",\"useUnicode\":\"true\"}")
    private String connConfig;

    @ApiModelProperty("描述")
    private String description;
}
