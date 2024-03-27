package org.apex.dataverse.port.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作业运行历史记录表
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class JobHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_history_id", type = IdType.AUTO)
    private Long jobHistoryId;

    /**
     * 作业ID。抽数作业或ETL作业的ID
     */
    private String jobId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * Connection ID
     */
    private Long connId;

    /**
     * ODPC链接的命令时为计算引擎ID
     */
    private Long engineId;

    /**
     * ODPC链接的命令时为计算引擎名称
     */
    private String engineName;

    /**
     * 命令ID
     */
    private String commandId;

    /**
     * 命令消息体，Json
     */
    private String command;

    /**
     * 作业提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 作业开始运行时间
     */
    private LocalDateTime runningTime;

    /**
     * 作业完成时间
     */
    private LocalDateTime finishedTime;

    /**
     * 作业状态运行状态
     */
    private Integer jobState;

    /**
     * 作业异常消息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
