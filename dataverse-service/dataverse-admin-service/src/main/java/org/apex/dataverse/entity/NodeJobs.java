package org.apex.dataverse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 调度任务点集中的作业
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NodeJobs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "node_job_id", type = IdType.AUTO)
    private Long nodeJobId;

    /**
     * 被加入调度中原作业编码，ETL作业或BDM作业
     */
    private String jobCode;

    /**
     * 作业类型，1：ETL Job, 2:BDM Job
     */
    private Integer jobType;

    /**
     * 作业名称
     */
    private String jobName;

    /**
     * 调度节点编码
     */
    private String nodeCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 作业加入时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;





    public NodeJobs() {
    }

    public NodeJobs(Long nodeJobId, String jobCode, int jobType, String jobName, String nodeCode, Integer env, LocalDateTime createTime) {
        this.nodeJobId = nodeJobId;
        this.jobCode = jobCode;
        this.jobType = jobType;
        this.jobName = jobName;
        this.nodeCode = nodeCode;
        this.env = env;
        this.createTime = createTime;
    }
}
