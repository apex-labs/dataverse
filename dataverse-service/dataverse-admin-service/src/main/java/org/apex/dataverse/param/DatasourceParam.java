package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DatasourceParam {

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty(value = "数据源类型ID", required = true)
    private Integer datasourceTypeId;

    @ApiModelProperty(value = "数据源类型名称", required = true)
    private String datasourceTypeName;

    @ApiModelProperty(value = "数据源名称", required = true)
    private String datasourceName;

    @ApiModelProperty(value = "数据源简称，缩写名称，字母数字下划线，非数字开头", required = true)
    private String datasourceAbbr;

    @ApiModelProperty(value = "数据源读写权限，1：只读，2：读写", required = true)
    private Integer datasourceReadWrite;

    @ApiModelProperty(value = "环境模式, 0:BASIC,1:DEV,2:PROD", required = true)
    private Integer env;

    @ApiModelProperty("数据源链接类型，不同链接类型的配置在不同的表中，1：JDBC数据源，2：kafka数据源")
    private Integer connType;

    @ApiModelProperty("JDBC连接配置")
    private JdbcSourceParam jdbcSourceParam;

    @ApiModelProperty("Kafka连接配置")
    private KafkaSourceParam kafkaSourceParam;

}
