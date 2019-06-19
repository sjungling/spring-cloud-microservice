package com.microweb.config;

import com.microweb.filter.ratelimit.RemoteAddrKeyResolver;
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