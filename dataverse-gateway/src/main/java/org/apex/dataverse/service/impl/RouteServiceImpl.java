package org.apex.dataverse.service.impl;

import org.apex.dataverse.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @ClassName RouteServiceImpl
 * @Description TODO
 * @Author wwd
 * @Date 2024/1/14 15:13
 **/
@Service
public class RouteServiceImpl implements RouteService, ApplicationEventPublisherAware {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void addRouter(RouteDefinition routerDefinition) {
        routeDefinitionWriter.save(Mono.just(routerDefinition)).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void updateRouter(RouteDefinition routerDefinition) {
        try {
            routeDefinitionWriter.delete(Mono.just(routerDefinition.getId())).subscribe();
            routeDefinitionWriter.save(Mono.just(routerDefinition)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(this));
        } catch (Exception e) {
            throw new RuntimeException("更新网关异常");
        }
    }

    @Override
    public void updateRouterList(List<RouteDefinition> definitions) {
        List<RouteDefinition> routeDefinitionsExits = routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (!CollectionUtils.isEmpty(routeDefinitionsExits)) {
            routeDefinitionsExits.forEach(routeDefinition -> {
                deleteRouter(routeDefinition);
            });
        }
        definitions.forEach(definition -> {
            updateRouter(definition);
        });
    }

    @Override
    public void deleteRouter(RouteDefinition routerDefinition) {
        routeDefinitionWriter.delete(Mono.just(routerDefinition.getId())).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
