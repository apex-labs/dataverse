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
    * 作业运行日志表
    * </p>
*
* @author danny
* @since 2023-12-20
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class JobLog implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * ID主键
            */
            @TableId(value = "job_log_id", type = IdType.AUTO)
    private Long jobLogId;

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
