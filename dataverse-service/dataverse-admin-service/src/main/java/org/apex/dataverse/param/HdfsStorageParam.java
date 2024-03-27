package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("Hdfs存储区配置参数")
@Data
public class HdfsStorageParam {

    @ApiModelProperty("Hdfs存储区配置主键")
    private Long hdfsStorageId;

    @ApiModelProperty("数据存储区主键")
    private Long storageId;
}
