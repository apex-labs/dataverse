package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KafkaInstanceDTO
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 09:47
 **/
@Data
public class KafkaSourceDTO {

    @ApiModelProperty("kafka实例ID")
    private Long kafkaSourceId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源环境，1:BASIC、2:DEV、3:PROD")
    private Integer env;

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String datasourceName;
}
