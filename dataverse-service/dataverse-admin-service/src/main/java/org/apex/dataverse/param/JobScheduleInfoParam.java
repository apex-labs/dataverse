package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class JobScheduleInfoParam {


    @ApiModelProperty("调度编码，同一调度在不同环境中编码相同")
    private String scheduleCode;

    @ApiModelProperty("当前作业编码")
    private String jobCode;

    @ApiModelProperty("当前作业类型 1：ETL Job, 2:BDM Job")
    private Integer jobType;

    @ApiModelProperty("当前节点编码")
    private String fromCode;


    @ApiModelProperty("当前环境编码")
    private Integer env;

}
