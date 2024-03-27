package org.apex.dataverse.port.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

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
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Engine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据计算引擎ID
     */
    @TableId(value = "engine_id", type = IdType.AUTO)
    private Long engineId;

    /**
     * 所属存储区ID
     */
    private Long storageId;

    /**
     * 引擎名称
     */
    private String engineName;

    /**
     * 启动Port的ID
     */
    private Long portId;

    /**
     * 启动Port的编码
     */
    private String portCode;

    /**
     * 启动Port的名称
     */
    private String portName;

    /**
     * 向注册中心注册时的注册编码，唯一
     */
    private String registryCode;

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
    private String engineType;

    /**
     * 引擎Jar包路径
     */
    private String engineJar;

    /**
     * 依赖的jar包
     */
    private String dependenceJars;

    /**
     * --master
     */
    private String master;

    /**
     * 引擎启动后的AppID
     */
    private String applicationId;

    /**
     * app name
     */
    private String applicationName;

    /**
     * 引擎状态, SUBMIT, RUNNING, FINISHED
     */
    private String engineState;

    /**
     * 提交过的作业总数
     */
    private Integer submitJobs;

    /**
     * 运行中作业总数
     */
    private Integer runningJobs;

    /**
     * 待处理作业数
     */
    private Integer pendingJobs;

    /**
     * 最大运行作业数
     */
    private Integer maxRunningJob;

    /**
     * Driver内存，单位G
     */
    private Integer driverMemory;

    /**
     * Driver CPU，单位核
     */
    private Integer driverCup;

    /**
     * 执行器内存，单位G
     */
    private Integer executorMemory;

    /**
     * 执行器CPU，单位核
     */
    private Integer executorCup;

    /**
     * 执行器数量
     */
    private Integer executors;

    /**
     * --deploy-mode
     */
    private String deployMode;

    /**
     * --queue
     */
    private String queue;

    /**
     * redirect out path
     */
    private String redirectOut;

    /**
     * redirect error path
     */
    private String redirectError;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 描述信息
     */
    private String description;

}
