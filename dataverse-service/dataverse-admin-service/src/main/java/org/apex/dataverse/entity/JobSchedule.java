package org.apex.dataverse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 调度任务
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class JobSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调度作业主键
     */
    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Long scheduleId;

    /**
     * 调度编码，同一调度在不同环境中编码相同
     */
    private String scheduleCode;

    /**
     * 调度作业名称
     */
    private String scheduleName;

    /**
     * 数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同
     */
    private String dvsCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 定时规则，cron表达式
     */
    private String cron;

    /**
     * 调度计划生命周期/状态
     * 调度编排中：31
     * 调度编排完成：32
     * 调度测试中：41
     * 调度测试完成(通过）：42
     * 已发布生产：51
     * 生产测试中：61
     * 生产测试完成(通过）：62
     * 已上线：71
     * 已下线：72
     */
    private Integer scheduleLifecycle;

    /**
     * 节点总数量
     */
    private Integer nodeNum;

    /**
     * 作业总数量
     */
    private Integer jobNum;

    /**
     * 调度起始时间
     */
    private LocalDateTime startTime;

    /**
     * 调度结束时间
     */
    private LocalDateTime endTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人所属租户ID
     */
    private Long tenantId;

    /**
     * 创建人所属租户名称【冗余】
     */
    private String tenantName;

    /**
     * 创建人所属部门ID
     */
    private Long deptId;

    /**
     * 创建人所属部门名称【冗余】
     */
    private String deptName;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;

    /**
     * 调度任务DAG对象
     */
    private String rawData;

    /**
     * 调度任务ID
     */
    private Long jobId;
}
