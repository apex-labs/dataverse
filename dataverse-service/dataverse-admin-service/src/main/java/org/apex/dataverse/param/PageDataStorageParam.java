package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

@ApiModel("存储区分页请求参数")
@Data
public class PageDataStorageParam {

    @ApiModelProperty("分页查询参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle")
    private Integer storageTypeId;

    @ApiModelProperty("数据存储区名称/别名")
    private String keyword;
}
