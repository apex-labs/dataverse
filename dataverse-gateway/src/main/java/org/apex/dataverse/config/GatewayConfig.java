package org.apex.dataverse.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @ClassName: GatewayConfig
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/13 15:38
 */
@Configuration
@Slf4j
@RefreshScope
public class GatewayConfig {

    public static final long DEFAULT_TIMEOUT = 30000;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String nacosNamespace;

    @Value("${nacos.gateway.route.config.data-id}")
    private String nacosRouteDataId;

    @Value("${nacos.gateway.route.config.group}")
    private String nacosRouteGroup;

    @Value("${spring.cloud.nacos.config.username}")
    private String nacosUserName;

    @Value("${spring.cloud.nacos.config.password}")
    private String nacosPassword;

    private ConfigService configService;

    private final ObjectMapper objectMapper;

    private final RouteService routeService;

    public GatewayConfig(ObjectMapper objectMapper, RouteService routeService) {
        this.objectMapper = objectMapper;
        this.routeService = routeService;
    }


    @PostConstruct
    public void init() {
        log.info("gateway route init...");
        try {
            configService = initConfigService();
            if (configService == null) {
                log.warn("initConfigService fail");
                return;
            }
            String configInfo = configService.getConfig(nacosRouteDataId, nacosRouteGroup, DEFAULT_TIMEOUT);
            log.info("获取网关当前配置:\r\n{}", configInfo);
            List<RouteDefinition> definitionList = objectMapper.readValue(configInfo, new TypeReference<List<RouteDefinition>>() {
            });
            for (RouteDefinition definition : definitionList) {
                log.info("update route : {}", definition.toString());
                routeService.addRouter(definition);
            }
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误", e);
        }
//        dynamicRouteByNacosListener(nacosRouteDataId, nacosRouteGroup);
    }


    public void dynamicRouteByNacosListener(String dataId, String group) {
        try {
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
//                    log.info("进行网关更新:\n\r{}", configInfo);
                    List<RouteDefinition> definitionList = null;
                    try {
                        definitionList = objectMapper.readValue(configInfo, new TypeReference<List<RouteDefinition>>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
//                    log.info("update route : {}", definitionList.toString());
                    routeService.updateRouterList(definitionList);
                }

                @Override
                public Executor getExecutor() {
                    log.info("getExecutor\n\r");
                    return null;
                }
            });
        } catch (NacosException e) {
            log.error("从nacos接收动态路由配置出错!!!", e);
        }
    }

    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", nacosServerAddr);
            properties.setProperty("namespace", nacosNamespace);
            properties.setProperty("username", nacosUserName);
            properties.setProperty("password", nacosPassword);
            properties.setProperty("group", nacosRouteGroup);

            return configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误", e);
            return null;
        }
    }

}
