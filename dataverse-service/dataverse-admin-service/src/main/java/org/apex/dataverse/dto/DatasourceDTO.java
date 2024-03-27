package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName DataSourceDTO
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 09:35
 **/
@Data
public class DatasourceDTO {
    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

    @ApiModelProperty("数据源类型名称")
    private String datasourceTypeName;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源简称，缩写名称，字母数字下划线，非数字开头")
    private String datasourceAbbr;

    @ApiModelProperty("数据源读写权限，1：只读，2：读写")
    private Integer datasourceReadWrite;

    @ApiModelProperty("环境模式, 1:BASIC,2:DEV,3:DEV,4:PROD")
    private Integer env;

    @ApiModelProperty("数据源链接类型，不同链接类型的配置在不同的表中，1：JDBC数据源，2：kafka数据源")
    private Integer connType;

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
