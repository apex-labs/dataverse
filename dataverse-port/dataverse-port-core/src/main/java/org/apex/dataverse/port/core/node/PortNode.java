package org.apex.dataverse.port.core.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.util.UCodeUtil;

import java.io.Serializable;

/**
 * @author Danny.Huo
 * @date 2023/6/2 14:15
 * @since 0.1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PortNode extends Node implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_MAX_CONN_NUM = 50;

    /**
     * 连接器ID
     */
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
     * 心跳频率，单位毫秒。 默认3000。
     */
    private Integer heartbeatHz;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名【冗余】
     */
    private String creatorName;

    public PortNode() {
        super.setNodeId(UCodeUtil.produce());
    }

    /**
     * 设置最大链接数
     *
     * @param maxConnNum Max connection number
     */
    public void setMaxConnCount(Integer maxConnNum) {
        if (null == maxConnNum) {
            this.maxDriverConns = DEFAULT_MAX_CONN_NUM;
        } else {
            this.maxDriverConns = maxConnNum;
        }
    }

    /**
     * 获取剩余链接数
     *
     * @return int
     */
    public int remainConnCount() {
        return maxDriverConns - connectedDrivers;
    }
}
