package com.microweb.config;

import com.microweb.config.properties.RabbitMQProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
//@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig implements RabbitListenerConfigurer {
    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        // Preconditions.checkNotNull(rabbitMQProperties.getPort(), "rabbitmq addresses can not be null.");

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
//        cachingConnectionFactory.setAddresses(rabbitMQProperties.getAddresses());
        cachingConnectionFactory.setPort(rabbitMQProperties.getPort());
        cachingConnectionFactory.setUsername(rabbitMQProperties.getUsername());
        cachingConnectionFactory.setPassword(rabbitMQProperties.getPassword());
//        cachingConnectionFactory.setVirtualHost(rabbitMQProperties.getVirtualHost() == null ? "/" : rabbitMQProperties.getVirtualHost());
        cachingConnectionFactory.setPublisherConfirms(rabbitMQProperties.isPublisherConfirms());
        cachingConnectionFactory.setPublisherReturns(rabbitMQProperties.isPublisherReturns());

        return cachingConnectionFactory;
    }

    @Bean
    /**
     * prototype:每次取得Bean的時候都會產生一個新的實例
     * 預設為singleton，若要設為消息確認模式時，需為prototype，否則ConfirmCallback為最後一個的RabbitTemplate */
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //預設為使用JDK，需另實作Serializable
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        //rabbitTemplate.setMandatory(true); //當投遞訊息錯誤(returnCallback)時，透過手動處理

        return rabbitTemplate;
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(new MappingJackson2MessageConverter());

        return messageHandlerMethodFactory;
    }

    //Consumer Message json format
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }
}