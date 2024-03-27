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
 * Dataverse port
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DvsPort implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 连接器ID
     */
    @TableId(value = "port_id", type = IdType.AUTO)
    private Long portId;

    /**
     * 编码，每个Port唯一
     */
    private String portCode;

    /**
     * 连接器名称
     */
    private String portName;

    /**
     * 向注册中心注册时的注册编码，唯一
     */
    private String registryCode;

    /**
     * 分组编码，当此编码不为空时，只有指定group_code的才可被对应code的driver链接。属于专属port
     */
    private String groupCode;

    /**
     * 分组名称【冗余】
     */
    private String groupName;

    /**
     * 连接器所在主机名
     */
    private String hostname;

    /**
     * 连接器所在IP
     */
    private String ip;

    /**
     * 连接器绑定的端口号
     */
    private Integer port;

    /**
     * Driver的最大链接数
     */
    private Integer maxDriverConns;

    /**
     * 已链接的driver数量
     */
    private Integer connectedDrivers;

    /**
     * 连接器状态。BUILD, ONLINE, PAUSE, OFFLINE
     */
    private String state;

    /**
     * 最近心跳时间
     */
    private LocalDateTime lastHeartbeat;

    /**
     * 心跳频率，单位毫秒。 默认3000。
     */
    private Integer heartbeatHz;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名【冗余】
     */
    private String creatorName;

    /**
     * 是否删除：0否，1：是
     */
    private Integer isDeleted;


}
