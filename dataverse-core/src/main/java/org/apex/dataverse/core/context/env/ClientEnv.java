package org.apex.dataverse.core.context.env;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @author : Danny.Huo
 * @date : 2023/2/16 19:30
 * @since : 0.1.0
 */
@Data
public class ClientEnv implements Serializable {

    public ClientEnv() {
        this.serverAddress = "127.0.0.1";
        this.serverPort = 20001;
        this.threads = 3;
        this.connTimeOutMs = 120000L;
    }

    public static ClientEnv newEnv() {
        return new ClientEnv();
    }

    /**
     * 服务端地址（IP/Host)
     */
    private String serverAddress;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 客户端线程数
     */
    private Integer threads;

    /**
     * 链接超时时间，毫秒
     */
    private Long connTimeOutMs;
}
