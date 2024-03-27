package org.apex.dataverse.core.context.env;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @author : Danny.Huo
 * @date : 2023/2/15 14:01
 * @since : 0.1.0
 */
@Data
public class ServerEnv implements Serializable {

    public static ServerEnv newEnv() {
        ServerEnv config = new ServerEnv();
        config.serverPort = 19999;
        return config;
    }

    public static ServerEnv newEnv(Integer serverPort) {
        ServerEnv config = new ServerEnv();
        config.serverPort = serverPort;
        return config;
    }

    public ServerEnv() {
        this.bossThreads = 3;
        this.workerThreads = 6;
    }

    /**
     * Boss线程数
     */
    private Integer bossThreads;

    /**
     * Worker线程数
     */
    private Integer workerThreads;

    /**
     * 端口号
     */
    private Integer serverPort;

}
