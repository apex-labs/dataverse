package org.apex.dataverse.port.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客户(Driver)端链接Port的链接记录表
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PortConn implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long portConnId;

    /**
     * Port的ID
     */
    private Long portId;

    /**
     * Port的编码
     */
    private String portCode;

    /**
     * 向注册中心注册时的注册编码，唯一
     */
    private String registryCode;

    /**
     * driver端的IP
     */
    private String ip;

    /**
     * driver端的hostname
     */
    private String hostname;

    /**
     * 链接状态
     */
    private String state;

    /**
     * 链接时间
     */
    private LocalDateTime connTime;

    /**
     * 断开链接时间
     */
    private LocalDateTime disConnTime;

    /**
     * 描述
     */
    private String description;


}
