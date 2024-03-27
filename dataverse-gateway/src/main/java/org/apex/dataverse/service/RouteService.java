package org.apex.dataverse.service;

import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

/**
 * @ClassName RouteService
 * @Description TODO
 * @Author wwd
 * @Date 2024/1/14 15:11
 **/
public interface RouteService {

    void addRouter(RouteDefinition routerDefinition);

    void updateRouter(RouteDefinition routerDefinition);

    void updateRouterList(List<RouteDefinition> definitions);

    void deleteRouter(RouteDefinition routerDefinition);
}
