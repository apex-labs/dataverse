package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName DataSourceInstanceDTO
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 09:46
 **/
@Data
public class DataSourceInstanceDTO {
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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("JDBC连接配置")
    private JdbcSourceDTO jdbcInstanceDTO;

    @ApiModelProperty("Kafka连接配置")
    private KafkaSourceDTO kafkaInstanceDTO;
}
