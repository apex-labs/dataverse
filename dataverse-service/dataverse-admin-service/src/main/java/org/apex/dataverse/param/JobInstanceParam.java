package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class JobInstanceParam {

    @ApiModelProperty("调度执行实例ID")
    private Integer jobInstanceId;

    @ApiModelProperty("调度实例编码，每次调度生成一 个唯一编码")
    private String scheduleInstanceCode;

    @ApiModelProperty("节点ID")
    private Long nodeId;

    @ApiModelProperty("调度作业节点编码")
    private String nodeCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("被加入调度中原作业编码，ETL作业或BDM作业")
    private String jobCode;

    @ApiModelProperty("作业类型，1：ETL Job, 2:BDM Job")
    private Boolean jobType;

    @ApiModelProperty("调度执行开始标识位")
    private String startOffset;

    @ApiModelProperty("调度执行结束标识位")
    private String endOffset;

    @ApiModelProperty("执行状态")
    private Boolean exeStatus;

    @ApiModelProperty("执行结果消息")
    private String exeResult;

}
