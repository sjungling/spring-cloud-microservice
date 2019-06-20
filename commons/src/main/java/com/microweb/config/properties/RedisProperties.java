package com.microweb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    /**
     * redis://user:password@example.com:6379
     */
    //private String url;

    private String host;

    private String password;

    private int port;

    private boolean ssl;

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
        private Integer maxActive;
        private Integer maxIdle;
        private Integer maxWait;
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