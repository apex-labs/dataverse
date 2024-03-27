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
 *
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class JobExeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "job_exe_log_id", type = IdType.AUTO)
    private Long jobExeLogId;

    /**
     * 作业ID
     */
    private String jobId;

    /**
     * 日志内容
     */
    private String logText;

    /**
     * 记录日志的时间
     */
    private LocalDateTime createTime;


}
