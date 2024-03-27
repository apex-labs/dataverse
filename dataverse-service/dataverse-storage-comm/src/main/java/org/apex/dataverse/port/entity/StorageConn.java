package org.apex.dataverse.port.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Port端链接引擎记录历史
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorageConn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 引擎客户端链接历史记录ID
     */
    private Long storageConnId;

    /**
     * 存储区ID
     */
    private Long storageId;

    /**
     * 数据计算引擎ID，当为hdfs存储区时，引擎不为空
     */
    private Long engineId;

    /**
     * 引擎名称
     */
    private String engineName;

    /**
     * 向注册中心注册时的注册编码，唯一
     */
    private String registryCode;

    /**
     * Port的ID
     */
    private Long portId;

    /**
     * Port的编码
     */
    private String portCode;

    /**
     * 连接的Channel ID
     */
    private String channelId;

    /**
     * 连接客户端的IP地址, port链接egine时port的ip
     */
    private String ip;

    /**
     * 连接客户端的host name, port链接egine时port的host
     */
    private String hostname;

    /**
     * 连接时间
     */
    private LocalDateTime connTime;

    /**
     * 断开连接时间
     */
    private LocalDateTime disConnTime;

    /**
     * 链接状态
     */
    private String state;

    /**
     * 描述
     */
    private String description;


}
