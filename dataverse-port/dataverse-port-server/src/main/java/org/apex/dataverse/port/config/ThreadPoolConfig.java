package org.apex.dataverse.port.config;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Danny.Huo
 * @date 2023/4/11 18:54
 * @since 0.1.0
 */
@Configuration
public class ThreadPoolConfig implements Serializable {

    @Value("${nexus.odpc.thread.pool.threads}")
    private int nThreads;

    @Value("${nexus.odpc.thread.pool.queue-size}")
    private int threadQueueSize;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(threadQueueSize),
                new DefaultThreadFactory("PORT-SERVICE"));
    }
}
