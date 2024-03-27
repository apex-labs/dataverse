package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DataStorageConfParam
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/23 14:44
 **/
@ApiModel("数据存储区配置参数")
@Data
public class DataStorageConfParam {

    @ApiModelProperty("jdbc存储区配置")
    private JdbcStorageParam jdbcStorageParam;

    @ApiModelProperty("hdfs存储区配置")
    private HdfsStorageParam hdfsStorageParam;
}
