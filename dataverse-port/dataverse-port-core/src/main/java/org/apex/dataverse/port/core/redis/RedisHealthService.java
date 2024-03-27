package org.apex.dataverse.port.core.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.stereotype.Component;

/**
 * @author danny
 * @date 2023/6/1 20:06
 * @since 0.1.0
 */
@Slf4j
@Component
public class RedisHealthService {

    /**
     * Redis version
     */
    private static final String REDIS_VERSION_PROPERTY = "redis_version";


    /**
     * Redis Connection  factory
     */
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 健康检查
     * @param builder builder
     */
    public void doHealthCheck(Health.Builder builder) {
        RedisConnection connection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        try {
            if (connection instanceof RedisClusterConnection) {
                ClusterInfo clusterInfo = ((RedisClusterConnection) connection).clusterGetClusterInfo();
                builder.up().withDetail("cluster_size", clusterInfo.getClusterSize())
                        .withDetail("slots_up", clusterInfo.getSlotsOk())
                        .withDetail("slots_fail", clusterInfo.getSlotsFail());
            } else {
                String version = connection.info().getProperty(REDIS_VERSION_PROPERTY);
                builder.up().withDetail("version", version);
            }
        } finally {
            RedisConnectionUtils.releaseConnection(connection, redisConnectionFactory, false);
        }
    }

    /**
     * Set redis connection factory
     * @param redisConnectionFactory redis connection factory
     */
    @Autowired
    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }
}
