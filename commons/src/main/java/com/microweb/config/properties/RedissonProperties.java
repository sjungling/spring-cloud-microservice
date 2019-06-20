package com.microweb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "distributedlock.redisson")
public class RedissonProperties {

    private String address;

    private String password;

    private int timeout = 3000;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize = 10;

    private int masterConnectionPoolSize = 250;

    private int slaveConnectionPoolSize = 250;

    private String[] sentinelAddress;

    private String masterName;
}