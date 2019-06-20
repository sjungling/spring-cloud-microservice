package com.microweb.gateway.config;

import com.microweb.gateway.filter.ratelimit.RemoteAddrKeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRateLimiterConfig {

    /**
     * ip 限流
     *
     * @return RemoteAddrKeyResolver
     */
    @Bean(name = RemoteAddrKeyResolver.BEAN_NAME)
    public RemoteAddrKeyResolver remoteAddrKeyResolver() {
        return new RemoteAddrKeyResolver();
    }
}