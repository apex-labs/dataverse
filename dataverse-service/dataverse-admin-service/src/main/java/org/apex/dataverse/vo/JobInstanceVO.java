package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobInstanceVO {

    @ApiModelProperty("调度执行实例ID")
    private Integer jobInstanceId;

    @ApiModelProperty("调度实例编码，每次调度生成一 个唯一编码")
    private String scheduleInstanceCode;

    @ApiModelProperty("作业执行实例编码，每次执行生成一个唯一code, job_code+start(yyyyMMddHHmm)+end(yyyyMMddHHmm) 如：xxx_202403160000_202403170000")
    private String jobInstanceCode;

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

    @ApiModelProperty("当前调度任务执行实例的起始偏移量, 如果时间，统一用默认格式yyyy-MM-dd HH:mm:ss")
    private String startOffset;

    @ApiModelProperty("当前调度任务执行实例的结束偏移量,如果时间，统一用默认格式yyyy-MM-dd HH:mm:ss")
    private String endOffset;

    @ApiModelProperty("执行状态")
    private Boolean exeStatus;

    @ApiModelProperty("执行结果消息")
    private String exeResult;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
