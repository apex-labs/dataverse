package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
public class KafkaSourceVO {

    @ApiModelProperty("kafka数据源ID")
    private Long kafkaSourceId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源环境，1:BASIC、2:DEV、3:PROD")
    private Integer env;

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String datasourceName;

}
