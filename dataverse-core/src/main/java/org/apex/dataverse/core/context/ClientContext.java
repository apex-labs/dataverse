package org.apex.dataverse.core.context;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.context.env.ClientEnv;

import java.io.Serializable;

/**
 * @author : Danny.Huo
 * @date : 2023/2/16 19:33
 * @since : 0.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientContext extends AbstractContext implements Serializable {

    /**
     * 默认实例化
     * @return ClientContext
     */
    public static ClientContext newContext() {
        return new ClientContext();
    }

    /**
     * 指定Env的实例化
     * @param env ClientEnv
     * @return ClientContext
     */
    public static ClientContext newContext(ClientEnv env) {
        return new ClientContext(env);
    }

    /**
     * 指定Evn和Pipeline实例化
     * @param env ClientEnv
     * @param pipeline Pipeline
     * @return ClientContext
     */
    public static ClientContext newContext(ClientEnv env, Pipeline pipeline) {
        return new ClientContext(env, pipeline);
    }

    /**
     * 默认无参构造
     */
    private ClientContext() {
        this.env = ClientEnv.newEnv();
        this.setPipeline(Pipeline.newDefaultPipeline());
    }

    /**
     * 指定Env有参构造
     * @param env  ClientEnv
     */
    private ClientContext(ClientEnv env) {
        this.env = env;
        this.setPipeline(Pipeline.newDefaultPipeline());
    }

    /**
     * 指定Evn和Pipeline有参构造
     * @param env ClientEnv
     * @param pipeline Pipeline
     */
    private ClientContext(ClientEnv env, Pipeline pipeline) {
        this.env = env;
        this.setPipeline(pipeline);
    }

    /**
     * Server相关的参数配置
     */
    private ClientEnv env;

    /**
     * 已链接上的Client Channel
     */
    private Channel channel;

    /**
     * 获取Server地址
     *
     * @return Server address "ip:port"
     */
    public String getServerAddress() {
        if (null == this.getEnv()) {
            return null;
        }
        return this.getEnv().getServerAddress()
                + ":" + this.getEnv().getServerPort();
    }
}
