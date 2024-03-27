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
 * 调度任务点集
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "node_id", type = IdType.AUTO)
    private Long nodeId;

    /**
     * 调度作业编码
     */
    private String scheduleCode;

    /**
     * 调度作业节点编码
     */
    private String nodeCode;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 1:开始节点，2：作业节点
     */
    private Integer nodeType;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    private LocalDateTime createTime;

    public ScheduleNode() {
    }

    public ScheduleNode(String scheduleCode, String nodeCode, String nodeName, Integer nodeType, Integer env, LocalDateTime createTime) {
        this.scheduleCode = scheduleCode;
        this.nodeCode = nodeCode;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.env = env;
        this.createTime = createTime;
    }
}
