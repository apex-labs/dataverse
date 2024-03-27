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
public class DatasourceVO {

    @ApiModelProperty("数据源实例ID")
    private Long datasourceId;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

    @ApiModelProperty("数据源类型名称")
    private String datasourceTypeName;

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String datasourceName;

    @ApiModelProperty("数据源简称，缩写名称，字母数字下划线，非数字开头")
    private String datasourceAbbr;

    @ApiModelProperty("数据源读写权限，1：只读，2：读写")
    private Integer datasourceReadWrite;

    @ApiModelProperty("数据源环境，1:BASIC、2:DEV、3:PROD")
    private Integer env;

    @ApiModelProperty("数据源链接类型，不同链接类型的配置在不同的表中，1：JDBC数据源，2：kafka数据源")
    private Integer connType;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

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

    @ApiModelProperty("JDBC配置展示")
    private JdbcSourceVO jdbcSourceVO;

    @ApiModelProperty("Kafka配置展示")
    private KafkaSourceVO kafkaSourceVO;

}
