package com.microweb.commons.config.properties;

import lombok.Data;

@Data
public class Swagger2Properties {

    private String basePackage = "com.microweb";

    private String termsOfServiceUrl = "https://github.com/allenfedd/spring-cloud-docker-microservice";

    private String ContactName = "allenfedd";

    private String ContactUrl = "https://github.com/allenfedd/";

    private String ContactEmail = "";

    private String apiVersion = "1.0";
}