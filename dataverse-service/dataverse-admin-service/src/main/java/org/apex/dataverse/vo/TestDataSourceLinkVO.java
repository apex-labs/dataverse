package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("测试数据源连接结果视图")
@Data
public class TestDataSourceLinkVO {

    @ApiModelProperty("实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）")
    private String instanceName;

    @ApiModelProperty("数据源环境，1：BASIC、2:DEV、3：TEST、4：PROD")
    private Integer env;

    @ApiModelProperty("数据源链接类型，不同链接类型的配置在不同的表中，1：JDBC数据源，2：kafka数据源")
    private Integer connType;

    @ApiModelProperty("测试连接结果 1:SUCCESS 2:FAIL")
    private Integer result;

    @ApiModelProperty("测试连接错误提示")
    private String errMsg;
}
