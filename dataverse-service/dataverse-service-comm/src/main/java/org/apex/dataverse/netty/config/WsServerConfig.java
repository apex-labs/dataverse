package org.apex.dataverse.netty.config;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.netty.conf
 * @className : WebSocketServerConfig
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 11:29
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@Data
@Builder
public class WsServerConfig implements Serializable {

    @Tolerate
    public WsServerConfig () {

    }

    /**
     * Server绑定端口
     */
    private Integer port;

    /**
     * 消息发送的最大长度
     */
    private Integer maxContentLength = 163840;

    /**
     * Boss线程数
     */
    private Integer bossThread;

    /**
     * Worker线程数
     */
    private Integer workerThreads;

    /**
     * 协议
     * 默认ws协议
     */
    private String protocol = "ws";

    /**
     * websocket path
     */
    private String websocketPath;
}
