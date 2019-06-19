package com.microweb.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@RefreshScope //動態刷新
@ConditionalOnExpression("!'{ignore}'.isEmpty()")
@ConfigurationProperties(prefix = "ignore") //prefix:指定屬性的名稱

public class FilterIgnorePropertiesConfig {
    private List<String> swaggerProviders = new ArrayList<>();
}