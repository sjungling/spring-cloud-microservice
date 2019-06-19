package com.microweb.filter.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 建立一個使用請求(request)的IP，來當作限流Key的KeyResolver。 預設的KeyResolver使用預設為PrincipalNameKeyResolver
 */
public class RemoteAddrKeyResolver implements KeyResolver {
    public static final String BEAN_NAME = "remoteAddrKeyResolver";

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        System.out.println("hostAddress:" + hostAddress);
        return Mono.just(hostAddress);
    }
}