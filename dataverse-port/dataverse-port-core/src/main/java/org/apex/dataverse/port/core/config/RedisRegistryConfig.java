package org.apex.dataverse.port.core.config;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apex.dataverse.port.core.exception.RegistryException;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Danny.Huo
 * @date 2021/7/29 11:14 AM
 * @since 0.1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus.odpc.registry")
public class RedisRegistryConfig {

    /**
     * 哨兵模式
     */
    private final static String SENTINEL = "sentinel";

    /**
     * 集群模式
     */
    private final static String CLUSTER = "cluster";


    /**
     * 注册中心URL
     */
    private String url;

    /**
     * 注册中心密码
     */
    private String password;

    /**
     * 节点地址端口分隔符
     */
    private final static String NODE_PAIR_SPLIT = ":";

    /**
     * 有参构造
     */
    public RedisRegistryConfig() {

    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public DefaultClientResources lettuceConnectionFactory() {
        return DefaultClientResources.create();
    }

    /**
     * 自定义redis connection factory
     *
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(DefaultClientResources defaultClientResources) throws RegistryException {
        RegistryInfo config = new RegistryInfo(this.url, this.password);

        // Lettuce客户端配置 连接池
        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                .clientResources(defaultClientResources).poolConfig(genericObjectPoolConfig(config)).build();

        LettuceConnectionFactory lettuceConnectionFactory;

        // 哨兵模式
        Set<RedisNode> nodes = new HashSet<>();
        if (config.getMode().equals(SENTINEL)) {
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
            redisSentinelConfiguration.setDatabase(config.getDatabase());
            redisSentinelConfiguration.setMaster(config.getMasterName());
            for (String node : config.getNodes()) {
                String[] nodePair = node.split(NODE_PAIR_SPLIT);
                RedisNode redisNode = new RedisNode(nodePair[0], Integer.parseInt(nodePair[1]));
                nodes.add(redisNode);
            }

            redisSentinelConfiguration.setSentinels(nodes);
            redisSentinelConfiguration.setPassword(config.getPassword());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration, lettuceClientConfiguration);
        }
        // 集群模式
        else if (config.getMode().equals(CLUSTER)) {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            for (String node : config.getNodes()) {
                String[] nodePair = node.split(NODE_PAIR_SPLIT);
                RedisNode redisNode = new RedisNode(nodePair[0], Integer.parseInt(nodePair[1]));
                nodes.add(redisNode);
            }
            redisClusterConfiguration.setClusterNodes(nodes);
            redisClusterConfiguration.setMaxRedirects(config.getMaxRedirects());
            redisClusterConfiguration.setPassword(config.getPassword());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
        }
        // 单机模式
        else {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setDatabase(config.getDatabase());
            String[] nodeInfo = config.getNodes()[0].split(NODE_PAIR_SPLIT);
            redisStandaloneConfiguration.setHostName(nodeInfo[0]);
            redisStandaloneConfiguration.setPort(Integer.parseInt(nodeInfo[1]));
            redisStandaloneConfiguration.setPassword(config.getPassword());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        }

        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    /**
     * 设置连接池
     *
     * @param config RegistryInfo
     * @return GenericObjectPoolConfig
     */
    private GenericObjectPoolConfig genericObjectPoolConfig(RegistryInfo config) {

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 配置文件中的池配置参数 如有新增池配置需要在这里设置
        poolConfig.setMinIdle(config.getMinIdle());
        poolConfig.setMaxTotal(config.getMaxActive());
        poolConfig.setMaxIdle(config.getMaxIdle());
        return poolConfig;
    }

    /**
     * Redis template相关配置
     * @param redisConnectionFactory Redis Connection Factory
     * @return RedisTemplate<String, Object>
     */
    @Bean(value = "objectTemplate")
    public RedisTemplate<String, Object> objectTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        // 值采用json序列化
        template.setValueSerializer(new StringRedisSerializer());

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }

    /**
     * Redis序列化值为字节
     *
     * @return RedisTemplate<String, byte[]>
     */
    @Bean(value = "byteTemplate")
    public RedisTemplate<String, byte[]> byteTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        // redis默认序列为数组
        template.setKeySerializer(new StringRedisSerializer());
        // value 采用byte数组序列化方式
        template.setValueSerializer(RedisSerializer.byteArray());
        template.afterPropertiesSet();

        return template;
    }

    /**
     * Test main
     * @param args arge
     * @throws RegistryException RegistryException
     */
    public static void main(String[] args) throws RegistryException {
        String url = "redis:cluster//10.25.19.1:6379,10.25.19.2:6379/0?minIdle=2&maxIdle=2&maxActive=2";
        RegistryInfo config = new RegistryInfo(url, "");
        System.out.println(config);
    }
}


