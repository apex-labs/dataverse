package org.apex.dataverse.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@Getter
@RefreshScope
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value( "${user.home}"+ "/" +"${xxl.job.executor.logpath}" )
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;
    @Value("${xxl.job.executor.maxConcurrentSize}")
    private int maxConcurrentSize;

    @Value("${xxl.job.executor.customize-executor-biz-bean-name}")
    private String customizeExecutorBizBeanName;

    @Value("${xxl.job.executor.task-timeout}")
    private Integer taskTimeout;

    @Value("${xxl.job.executor.task-fail-retry-count}")
    private Integer taskFailRetryCount;
}