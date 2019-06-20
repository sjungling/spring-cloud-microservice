package com.microweb.mq.sender;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microweb.constant.RabbitMQConstants;
import com.microweb.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RabbitMQSender extends RabbitMQSenderCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void createFlashSaleOrder(String msg) {
        log.debug("createFlashSaleOrder Sender Msg:{}", msg);

        Map<String, Object> map = FastJsonUtils.parseToGenericObject(msg, new TypeReference<HashMap<String, Object>>() {
        });

        //訊息唯一ID
        CorrelationData correlationData = new CorrelationData(String.valueOf(map.get("messageId")));
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ORDER_CREATE_DIRECT_EXCHANGE, RabbitMQConstants.ORDER_CREATE_DIRECT_RK, msg, correlationData);
    }
}