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
 * 计算引擎注册表
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据计算引擎ID
     */
    @TableId(value = "data_engine_id", type = IdType.AUTO)
    private Long dataEngineId;

    /**
     * 引擎名称
     */
    private String dataEngineName;

    /**
     * 引擎Driver所在的机器主机名
     */
    private String hostname;

    /**
     * 引擎Driver所在的机器IP
     */
    private String ip;

    /**
     * 引擎Driver中server绑定的端口号
     */
    private Integer port;

    /**
     * 引擎类型，1：Spark批处理引擎，2：Spark流式引擎，3：Flink批处理引擎，4：Flink流式处理引擎
     */
    private Integer engineType;

    /**
     * 引擎状态
     */
    private Integer engineStatus;

    /**
     * 提交过的作业总数
     */
    private Integer submitedJobNum;

    /**
     * 运行中作业总数
     */
    private Integer runningJobNum;

    /**
     * 待处理作业数
     */
    private Integer pendingJobNum;

    /**
     * 最大运行作业数
     */
    private Integer maxRunningJobNum;

    /**
     * 内存容量
     */
    private Integer memoryCapacity;

    /**
     * CPU容量
     */
    private Integer cpuCapacity;

    /**
     * Driver内存容量
     */
    private Integer driverMemoryCapacity;

    /**
     * Driver CPU容量
     */
    private Integer driverCupCapacity;

    /**
     * 执行器内存容量
     */
    private Integer executorMemoryCapacity;

    /**
     * 执行器CPU容量
     */
    private Integer executorCupCapacity;

    /**
     * 执行器数量
     */
    private Integer executorNum;

    /**
     * 客户端链接数量
     */
    private Integer clientNum;

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


}
