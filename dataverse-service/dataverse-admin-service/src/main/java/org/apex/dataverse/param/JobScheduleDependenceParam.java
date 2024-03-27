package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class JobScheduleDependenceParam {


    @ApiModelProperty("调度编码，同一调度在不同环境中编码相同")
    private String scheduleCode;

    @ApiModelProperty("当前作业编码")
    private String jobCode;

    @ApiModelProperty("依赖节点编码")
    private String dependenceCode;

    @ApiModelProperty("是否生效:生效 1 ,失效 0")
    private Integer isValid;

    @ApiModelProperty("环境编码")
    private Integer env;
}
