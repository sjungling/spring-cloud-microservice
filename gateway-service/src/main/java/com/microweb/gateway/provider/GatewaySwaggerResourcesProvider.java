//注入 route 到 SwaggerResource

package com.microweb.gateway.provider;

import com.microweb.gateway.config.FilterIgnorePropertiesConfig;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary //多個Bean中，優先選擇使用的Bean
@AllArgsConstructor
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {
    // swagger2 預設的uri
    public static final String API_URI = "/v2/api-docs";

    public static final String EUREKA_SUB_PRIX = "CompositeDiscoveryClient_";

    private final DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator;

    private final RouteLocator routeLocator;

    private final GatewayProperties gatewayProperties;

    private final FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        List<String> routes = new ArrayList<>();

        /**
         * 方法1
         * 透過 DiscoveryClientRouteDefinitionLocator 中取得 routes
         * ex: localhost:8081(gateway port)/FLASHSALE-SERVICE(service name)/v2/api-docs
         *
         *  discoveryClientRouteDefinitionLocator.getRouteDefinitions().subscribe(routeDefinition -> {
         *             resources.add(swaggerResource(routeDefinition.getId().substring(EUREKA_SUB_PRIX.length()),
         *                     routeDefinition.getPredicates().get(0).getArgs().get("pattern").replace("/**", API_URI)));
         *         });
         *
         */

        /**
         * 法2 透過 routeLocator 取得 Gateway的route
         */
        //Add the default swagger resource that correspond to the gateway's own swagger doc
        //resources.add(swaggerResource("default", API_URI));

        //取得 Gateway的route
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));

        //Add the registered microservices swagger docs as additional swagger resources
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                        .filter(predicateDefinition -> !filterIgnorePropertiesConfig.getSwaggerProviders().contains(routeDefinition.getId()))
                        //not provider swagger docs
                        .forEach(predicateDefinition -> resources.add(swaggerResource(routeDefinition.getId(),
                                predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                        .replace("/**", API_URI))))
                );

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");

        return swaggerResource;
    }
}