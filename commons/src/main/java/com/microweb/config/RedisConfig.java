package com.microweb.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microweb.config.properties.RedisProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
//@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {
    @Autowired
    private RedisProperties redisProperties;

    /**
     * Java資料儲存至Redis有三種方式
     * 1. 將資料轉成json格式儲存。
     * 2. 設定 redis 的Value＆HashValue序列化方式為 JDK序列化，儲存於redis資料為二進位資料。
     * 3. 設定 redis 的Value＆HashValue序列化方式 Jackson序列化，儲存於redis資料為json字串資料。
     */

    //jackson序列化
    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        //指定要序列化的範圍：field,get,set 及 修飾範圍：ANY -> public & private
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);

        return serializer;
    }

    /**
     * RedisTemplate<String, Object> 泛型 預設為RedisTemplate<Object, Object>
     * <p>
     * 注意: 方法(function)一定要為redisTemplate同名，因 @Bean註釋是依據function來配置這個Bean的name
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory, Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        //String 方式序列化Key
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //使用 Jackson json方式序列化Value，預設使用JDKSerializationRedisSerializer
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }

    // Lettuce(Redis Client) Standalone Mode
    @ConditionalOnProperty({"spring.redis.lettuce.pool.max-active", "spring.redis.lettuce.pool.max-idle", "spring.redis.lettuce.pool.max-wait", "spring.redis.lettuce.pool.min-idle"})
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .poolConfig(new GenericObjectPoolConfig())
                .build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);

        return lettuceConnectionFactory;
    }

    /*
    // Lettuce(Redis Client) Cluster Mode
    @ConditionalOnProperty(value = "spring.redis.cluster.nodes")
    @Bean
    LettuceConnectionFactory lettuceConnectionFactoryCluster() {

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());

        redisClusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        redisClusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects().intValue());

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .poolConfig(new GenericObjectPoolConfig())
                .build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);

        return lettuceConnectionFactory;
    }
    */

    /*
    //GenericObjectPoolConfig(Connection Pool) 設定
    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();

        genericObjectPoolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait());

        return genericObjectPoolConfig;
    }
    */
}