package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NodeJobsParam {


    @ApiModelProperty("节点编码 首次添加节点为空")
    private String nodeCode;

    @ApiModelProperty("调度任务点集节点id")
    private Long nodeJobId;

    @ApiModelProperty("被加入调度中原作业编码，ETL作业或BDM作业")
    private String jobCode;

    @ApiModelProperty("作业类型，1：ETL Job, 2:BDM Job")
    private Integer jobType;

    @ApiModelProperty("作业名称")
    private String jobName;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;

}
