package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: EtlJobVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/15 15:09
 */
@ApiModel
@Data
public class EtlJobVO {

    @ApiModelProperty("集成任务主键ID")
    private Long etlJobId;

    @ApiModelProperty("集成任务编码")
    private String etlJobCode;

    @ApiModelProperty("集成任务数据空间编码")
    private String dvsCode;

    @ApiModelProperty("集成任务数据域编码")
    private String dataRegionCode;

    @ApiModelProperty("集成任务名称")
    private String etlJobName;

    @ApiModelProperty("集成任务分组编码")
    private String etlGroupCode;

    @ApiModelProperty("集成任务环境,0:Basic,1:Dev,2:Prod")
    private Integer env;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("数据源编码")
    private String datasourceCode;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("")
    private String cron;

    @ApiModelProperty("作业生命周期/状态  作业开发中（草稿）：11 作业开发完成：12 作业测试中：21 作业测试完成(通过）：22 调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer jobLifecycle;

    @ApiModelProperty("是否删除 0:否, 1:是")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("发布状态, BASIC/DEV/PROD")
    private String deployStatus;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("集成任务执行结果")
    private String exeResult;

    @ApiModelProperty("集成任务表数量")
    private Integer tableCount;

    @ApiModelProperty("数据集成表信息")
    private List<EtlJobTableVO> etlJobTableVOList;

}
