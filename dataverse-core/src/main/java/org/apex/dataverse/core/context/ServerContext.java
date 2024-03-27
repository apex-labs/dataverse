package org.apex.dataverse.core.context;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.context.env.ServerEnv;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Danny.Huo
 * @date : 2023/2/16 18:58
 * @since : 0.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerContext extends AbstractContext implements Serializable {

    /**
     * 默认实例化
     * @return ServerContext
     */
    public static ServerContext newContext() {
        return new ServerContext();
    }

    /**
     * 指定Env的实例化
     * @param env ServerEnv
     * @return ServerContext
     */
    public static ServerContext newContext(ServerEnv env) {
        return new ServerContext(env);
    }

    /**
     * 指定Evn和Pipeline的实例化
     * @param env ServerEnv
     * @param pipeline Pipeline
     * @return ServerContext
     */
    public static ServerContext newContext(ServerEnv env, Pipeline pipeline) {
        return new ServerContext(env, pipeline);
    }

    /**
     * Server相关的参数配置
     */
    private ServerEnv env;

    /**
     * 已链接上的Client Channel
     */
    private Map<String, Channel> channels;


    /**
     * 默认无参构造
     */
    private ServerContext() {
        this.env = ServerEnv.newEnv();
        this.setPipeline(Pipeline.newDefaultPipeline());
        this.channels = new ConcurrentHashMap<>();
    }

    /**
     * 指定Evn的有参构造
     * @param env ServerEnv
     */
    private ServerContext(ServerEnv env) {
        this.env = env;
        this.setPipeline(Pipeline.newDefaultPipeline());
        this.channels = new ConcurrentHashMap<>();
    }

    /**
     * 指定Env和Pipeline的有参构造
     * @param env ServerEnv
     * @param pipeline Pipeline
     */
    private ServerContext(ServerEnv env, Pipeline pipeline) {
        this.env = env;
        this.setPipeline(pipeline);
        this.channels = new ConcurrentHashMap<>();
    }

    /**
     * 加入新Channel
     * @param channel Channel
     */
    public void addChannel(Channel channel) {
        this.channels.put(channel.id().toString(), channel);
    }

    /**
     * 移除channel
     * @param channel Channel
     * @return Channel
     */
    public Channel removeChannel(Channel channel) {
        return channels.remove(channel.id().toString());
    }

    /**
     * 根据ChannelId查Channel
     * @param channelId String
     * @return Channel
     */
    public Channel getChannel(String channelId) {
        return channels.get(channelId);
    }
}
