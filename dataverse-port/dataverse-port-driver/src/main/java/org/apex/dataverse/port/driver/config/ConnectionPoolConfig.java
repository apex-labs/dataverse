package org.apex.dataverse.port.driver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apex.dataverse.port.core.enums.RegistryKeyEnum;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.core.redis.RedisRegistryService;
import org.apex.dataverse.port.core.node.PortNode;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Danny.Huo
 * @date 2023/2/22 11:41
 * @since 0.1.0
 */
@Data
@Configuration
public class ConnectionPoolConfig {

    /**
     * 核心链接数
     */
    private Integer core = 5;

    /**
     * 最大链接数
     */
    private Integer max = 5;

    /**
     * Max blocking size
     */
    private int maxBlockingSize = 100;

    /**
     * Keep alive time
     */
    private long keepAliveTime = 0L;

    /**
     * Odpc client threads
     */
    private Integer clientThreads = 2;

    /**
     * Connection pool capacity
     */
    @Value("${nexus.port.connection.pool.capacity}")
    private int connPoolCapacity = 2000;

    /**
     * Connection queue
     */
    private List<PortNode> nodes = new ArrayList<>();

    /**
     * ExecutorService
     */
    private ExecutorService executorService;

    /**
     * Redis registry service
     */
    private RedisRegistryService redisRegistryService;

    /**
     * Gets a node with the highest number of free connections remaining
     * @return PortNode
     */
    public synchronized PortNode routePort(String storageId, String groupCode) throws NoPortNodeException, JsonProcessingException {
        PortNode goal = null;
        nodes = redisRegistryService.findPort(RegistryKeyEnum.buildPortFindKey(storageId, groupCode));
        for (PortNode node : nodes) {
            if (null == goal) {
                goal = node;
                continue;
            }
            if (node.remainConnCount() > goal.remainConnCount()) {
                goal = node;
            }
        }
        return goal;
    }

    @Bean
    public ExecutorService executorService() {
        executorService = new ThreadPoolExecutor(core, max,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(maxBlockingSize),
                new DefaultThreadFactory("PORT-THREAD"));
        return executorService;
    }

    @Autowired
    public void setRedisRegistryService(RedisRegistryService redisRegistryService) {
        this.redisRegistryService = redisRegistryService;
    }
}
