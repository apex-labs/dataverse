package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("Kafka实例请求参数")
@Data
public class KafkaSourceParam {

    @ApiModelProperty("kafka实例ID")
    private Long kafkaSourceId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty(value = "环境模式, 1:BASIC,2:DEV,3:TEST,4:PROD", required = true)
    private Integer env;

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String datasourceName;
}
