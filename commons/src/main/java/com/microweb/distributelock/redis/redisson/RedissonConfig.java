package com.microweb.distributelock.redis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
//@ConditionalOnClass(Config.class)
//@EnableConfigurationProperties(RedissonPro)
public class RedissonConfig {

    /**
     * 文件方式配置(Declarative configuration)
     * 有 json,yaml,spring xml 三種
     */

    /*
    @Value("classpath:/config/redisson-single.yml")
    Resource configFile;
    */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        //Config config = Config.fromYAML(configFile.getInputStream());

        Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson-single.yml"));

        return Redisson.create(config);
    }

/*
    /**
     * ConditionalOnProperty : 此注釋目的為控制某configuration是否生效。
     * <p>
     * 具體操作為通過 name和havingValue這兩個屬性來實現的，其中name來至application.properties中讀取的某屬性值，
     * 若該值為空，則返回false，若有值的話，進一步與havingValue的值進行比對，值相同返回true，否則 false
     * <p>
     * 返回值為true時，該configuration生效，否則不生效
     */

/*
    // 程式配置(Programmatically configuration)
    @Autowired
    private final RedissonProperties redissonProperties;

    @Bean
    @ConditionalOnProperty(name = "redisson.address")
    public RedissonClient singleRedissonClient() {

        Config config = new Config();

        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());

        if (StringUtils.isNoneBlank(redissonProperties.getPassword())) {
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnProperty(name = "redisson.master-name")
    public RedissonClient sentinelRedissonClient() {

        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .addSentinelAddress(redissonProperties.getSentinelAddress())
                .setMasterName(redissonProperties.getMasterName())
                .setTimeout(redissonProperties.getTimeout())
                .setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize())
                .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize())

        if (StringUtils.isNoneBlank(redissonProperties.getPassword())) {
            sentinelServersConfig.setPassword(redissonProperties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnProperty(name = "redisson.addresses")
    public RedissonClient clusterRedissonClient() {

        Config config = new Config();

        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .setScanInterval(2000)
                .addNodeAddress(redissonProperties.getAddress());

        if (StringUtils.isNoneBlank(redissonProperties.getPassword())) {
            clusterServersConfig.setPassword(redissonProperties.getPassword());
        }

        return Redisson.create(config);
    }
*/

    /**
     * Configure redis lock instance
     * <p>
     * 注入實例至 RedissonLockUtil
     */

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RedissonLockUtil redissonLockUtil(RedissonClient redissonClient) {
        RedissonLockUtil redissonLockUtil = new RedissonLockUtil();
        redissonLockUtil.setRedissonClient(redissonClient);

        return redissonLockUtil;
    }
}