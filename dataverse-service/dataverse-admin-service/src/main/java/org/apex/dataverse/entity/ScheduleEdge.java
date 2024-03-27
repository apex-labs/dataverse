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
 * 调度任务边集
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleEdge implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 边ID
     */
    @TableId(value = "edge_id", type = IdType.AUTO)
    private Long edgeId;

    /**
     * 边编码
     */
    private String edgeCode;

    /**
     * 调度作业编码
     */
    private String scheduleCode;

    /**
     * From节点的code
     */
    private String fromNode;

    /**
     * To节点的code
     */
    private String toNode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否生效，1：有效、0：失效
     */
    private Integer isValid;

    public ScheduleEdge() {
    }

    public ScheduleEdge(String edgeCode, String scheduleCode, String fromNode, String toNode, Integer env, LocalDateTime createTime, Integer isValid) {
        this.edgeCode = edgeCode;
        this.scheduleCode = scheduleCode;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.env = env;
        this.createTime = createTime;
        this.isValid = isValid;
    }
}
