package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据源实例请求参数")
@Data
public class DataSourceInstanceParam {

    @ApiModelProperty("数据源实例ID")
    private Long datasourceInstanceId;

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String instanceName;

    @ApiModelProperty("数据源环境，1：BASIC、2:DEV、3：TEST、4：PROD")
    private Integer env;

    @ApiModelProperty("数据源链接类型，不同链接类型的配置在不同的表中，1：JDBC数据源，2：kafka数据源")
    private Integer connType;

    @ApiModelProperty("JDBC连接配置")
    private JdbcSourceParam jdbcInstanceParam;

    @ApiModelProperty("Kafka连接配置")
    private KafkaSourceParam kafkaInstanceParam;

}
