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
 * 作业记录表
 * </p>
 *
 * @author danny
 * @since 2024-01-13
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
     * 数据计算引擎ID
     */
    private Long dataEngineId;

    /**
     * 数据引擎名称
     */
    private String dataEngineName;

    /**
     * 命令ID
     */
    private String commandId;

    /**
     * Netty 链接的Channel ID
     */
    private String channelId;

    /**
     * 客户端ID/引擎ID
     */
    private Long clientId;

    /**
     * 作业提交时间
     */
    private LocalDateTime jobSubmitTime;

    /**
     * 作业开始运行时间
     */
    private LocalDateTime jobRunningTime;

    /**
     * 作业完成时间
     */
    private LocalDateTime jobFinishedTime;

    /**
     * 作业状态
     */
    private Integer jobStatus;

    /**
     * 作业异常消息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
