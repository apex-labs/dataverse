package org.apex.dataverse.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 计算引擎客户端连接历史表
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ClientHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 引擎客户端链接历史记录ID
     */
    private Long clientHistoryId;

    /**
     * 数据计算引擎ID
     */
    private Long dataEngineId;

    /**
     * 连接的Channel ID
     */
    private Integer channelId;

    /**
     * 连接时间
     */
    private LocalDateTime connTime;

    /**
     * 断开连接时间
     */
    private LocalDateTime disconnTime;

    /**
     * 连接客户端的IP地址
     */
    private String clientIp;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;


}
