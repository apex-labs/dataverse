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
public class DataStorageVO {

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty("存储区名称，英文名称")
    private String storageName;

    @ApiModelProperty("存储区别名，显示名称")
    private String storageAlias;

    @ApiModelProperty("存储区简称，英文缩写")
    private String storageAbbr;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private Integer storageTypeId;

    @ApiModelProperty("存储区链接方式，1：HDFS，2：JDBC链接")
    private Integer storageConnType;

    @ApiModelProperty("存储区描述信息")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人所属租户ID")
    private Long tenantId;

    @ApiModelProperty("创建人所属租户名称【冗余】")
    private String tenantName;

    @ApiModelProperty("创建人所属部门ID")
    private Long deptId;

    @ApiModelProperty("创建人所属部门名称【冗余】")
    private String deptName;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;

}
