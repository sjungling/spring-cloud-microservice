package com.microweb.config;

import com.microweb.constant.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMQConfig {

    /**
     * create exchange
     */
    /*
    @Bean
    public DirectExchange orderDirectExchange() {
        //return new DirectExchange(RabbitMQConstants.ORDER_DIRECT_EXCHANGE);
        return (DirectExchange) ExchangeBuilder.directExchange(RabbitMQConstants.ORDER_DIRECT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    TopicExchange orderTopicExchange() {
        return (TopicExchange) ExchangeBuilder.directExchange(RabbitMQConstants.ORDER_TOPIC_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    FanoutExchange orderFanoutExchange() {
        return (FanoutExchange) ExchangeBuilder.directExchange(RabbitMQConstants.ORDER_TOPIC_EXCHANGE)
                .durable(true)
                .build();
    }
    */
    @Bean
    public DirectExchange orderCreateDirectExchange() {
        return (DirectExchange) ExchangeBuilder.directExchange(RabbitMQConstants.ORDER_CREATE_DIRECT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue orderCreateQueue() {
        return new Queue(RabbitMQConstants.ORDER_CREATE_QUEUE);
    }

    @Bean
    public Binding orderCreateBinging() {
        return BindingBuilder.bind(orderCreateQueue()).to(orderCreateDirectExchange()).with(RabbitMQConstants.ORDER_CREATE_DIRECT_RK);
    }
}