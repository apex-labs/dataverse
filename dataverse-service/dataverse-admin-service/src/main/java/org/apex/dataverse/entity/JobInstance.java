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
public class JobInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_instance_id", type = IdType.AUTO)
    private Integer jobInstanceId;

    /**
     * 调度实例编码，外键引用调度实例中的调度实例编码
     */
    private String scheduleInstanceCode;

    /**
     * 作业执行实例编码，每次执行生成一个唯一code, job_code+start(yyyyMMddHHmm)+end(yyyyMMddHHmm)
     * 如：xxx_202403160000_202403170000
     */
    private String jobInstanceCode;

    /**
     * 节点ID
     */
    private Long nodeId;

    /**
     * 调度作业节点编码
     */
    private String nodeCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 被加入调度中原作业编码，ETL作业或BDM作业
     */
    private String jobCode;

    /**
     * 作业类型，1：ETL Job, 2:BDM Job
     */
    private int jobType;

    /**
     * 当前调度任务执行实例的起始偏移量, 如果时间，统一用默认格式yyyy-MM-dd HH:mm:ss
     */
    private String startOffset;

    /**
     * 当前调度任务执行实例的结束偏移量,如果时间，统一用默认格式yyyy-MM-dd HH:mm:ss
     */
    private String endOffset;

    /**
     * 执行状态
     */
    private Integer exeStatus;

    /**
     * 执行结果消息
     */
    private String exeResult;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

//    public JobInstance() {
//    }
//
//    public JobInstance(Integer jobInstanceId, String scheduleInstanceCode, Long nodeId, String nodeCode, Integer env, String jobCode, int jobType, LocalDateTime createTime) {
//        this.jobInstanceId = jobInstanceId;
//        this.scheduleInstanceCode = scheduleInstanceCode;
//        this.nodeId = nodeId;
//        this.nodeCode = nodeCode;
//        this.env = env;
//        this.jobCode = jobCode;
//        this.jobType = jobType;
//        this.createTime = createTime;
//    }
}
