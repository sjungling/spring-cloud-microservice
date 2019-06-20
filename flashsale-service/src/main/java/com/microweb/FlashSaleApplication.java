package com.microweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@ComponentScan({"com.microweb.common", "com.microweb.flashsale"})
@EnableSwagger2
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class FlashSaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlashSaleApplication.class, args);
    }
}