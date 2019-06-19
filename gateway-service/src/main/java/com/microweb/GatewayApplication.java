package com.microweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//@EnableEurekaClient   //only Spring Cloud Netflix Eureka
@EnableDiscoveryClient  //Spring Cloud Netflix Eureka, Spring Cloud Consul Discovery, Spring Cloud Zookeeper Discovery
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}