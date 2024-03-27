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
public class JdbcSourceVO {

    @ApiModelProperty("JDBC数据源ID")
    private Long jdbcSourceId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源环境，1:BASIC、2:DEV、3:PROD")
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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
