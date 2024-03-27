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

    @ApiModelProperty("链接配置JSON，如：{\"serverTimezone\":\"Asia/Shanghai\",\"useUnicode\":\"true\"}")
    private String connConfig;

    @ApiModelProperty("存储区类型，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private Integer storageType;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
