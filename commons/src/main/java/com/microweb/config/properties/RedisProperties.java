package com.microweb.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * redis://user:password@example.com:6379
 */
@Data
@Configuration
//@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
    //private String url;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    private boolean ssl;

    @Value("${spring.redis.timeout}")
    private Integer timeout;

    /**
     * Database index used by the connection factory.
     */
    private int database;

    private Pool pool;

    private Cluster cluster;

    private Lettuce lettuce;

    @Data
    public static class Pool {
        @Value("${spring.redis.lettuce.pool.max-active}")
        private Integer maxActive;

        @Value("${spring.redis.lettuce.pool.max-idle}")
        private Integer maxIdle;

        @Value("${spring.redis.lettuce.pool.max-wait}")
        private Integer maxWait;

        @Value("${spring.redis.lettuce.pool.min-idle}")
        private Integer minIdle;
    }

    @Data
    public static class Cluster {

        /**
         * list of "host:port" , is required to have at least one entry.
         */
        private List<String> nodes;

        private Integer maxRedirects;
    }

    @Data
    public static class Lettuce {

        private Integer shutdownTimeout;

        private Pool pool;
    }
}