package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NodeJobsDTO {

    @ApiModelProperty("调度任务点集节点id")
    private Long nodeJobId;

    @ApiModelProperty("被加入调度中原作业编码，ETL作业或BDM作业")
    private String jobCode;

    @ApiModelProperty("作业类型，1：ETL Job, 2:BDM Job")
    private int jobType;

    @ApiModelProperty("作业名称")
    private String jobName;

    @ApiModelProperty("调度节点编码")
    private String nodeCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

}
