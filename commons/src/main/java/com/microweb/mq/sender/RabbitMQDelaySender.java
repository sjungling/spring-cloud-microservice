package com.microweb.mq.sender;

import com.microweb.constant.RabbitMQConstants;
import com.microweb.utils.FastJsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RabbitMQDelaySender /*extends RabbitMQSenderCallback*/ {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //未付款超過30分鐘，關閉訂單
    public void orderClose(String msg) {
        log.debug("orderClose, Msg:{}", msg);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("訂單已成立，30分鐘後若未付款則會關閉訂單,目前時間: " + sdf.format(new Date()));


        Map<String, Object> map = FastJsonUtils.parseToGenericObject(msg, new TypeReference<HashMap<String, Object>>() {
        });

        String messageId = (String) map.get("messageId");

        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.convertAndSend(RabbitMQConstants.DelayQueue.DEAD_LETTER_EXCHANGE, RabbitMQConstants.DelayQueue.DELAY_ROUTING_KEY, msg, message -> {
            //訊息唯一ID
            message.getMessageProperties().setMessageId(messageId);
            message.getMessageProperties().setExpiration(String.valueOf(RabbitMQConstants.ORDER_UNPAID_CLOSE_TIMEOUT));
            message.getMessageProperties().setTimestamp(new Date());

            System.out.println("MessageProperties:" + message.toString());

            return message;
        });
    }





    /**
     * confirmCallback 在以下情況會發生
     * 當連線無虞時：
     * 1. Message 已送至Queue , Ack = true
     * 2. 交換機(Exchange)錯誤 , Ack = false
     * 3. returnCallback 完成後 , Ack = true
     */
    /*private static*/ final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println(correlationData.toString());
            /*
            String messageId = correlationData.getId();
            // broker服務器已確認訊息成功投遞到指定的佇列的callback(此時訊息已進入佇列，但不代表訊息消費者已經監聽成功並完成訊息處理)
            System.out.println("===========RabbitMQ ConfirmCallback===========");
            System.out.println("correlationData = " + FastJsonUtils.parseToString(correlationData));
            System.out.println("ack = " + ack);
            System.out.println("cause = " + cause);
            System.out.println("===========RabbitMQ ConfirmCallback===========");

            //發送訊息到broker成功
            if (ack) {
                log.info("Message ConfirmCallback successfully, messageId: {}, ack: {}, cause: {}", messageId, ack, cause);
                brokerMessageLogUtils.updateStatusByMessageId(messageId, BrokerMessageLogStatusEnum.MESSAGE_SEND_SUCCESS, new Date());
            } else {
                //需要實作重新發送或補償方式(cronjob+brokerMessageLog)
                log.info("Message ConfirmCallback failed, messageId: {}, ack: {}, cause: {}", messageId, ack, cause);
            }
            */
        }
    };

    /**
     * 無法連線：
     * returnCallback 是發生當指定的Exchange存在，但通過RoutingKey找不到對應的Queue，導致broker無法將Message送至指定的Queue
     * <p>
     * 注意： 當returnCallback完成後，會同時執行confirmCallBack並且ack會是true
     */

    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("==========RabbitMQ ReturnCallback==========");
            System.out.println("com.microweb.flashsale.message = " + message);
            System.out.println("replyCode = " + replyCode);
            System.out.println("replyText = " + replyText);
            System.out.println("exchange = " + exchange);
            System.out.println("routingKey = " + routingKey);
            System.out.println("==========RabbitMQ ReturnCallback==========");
            log.info("Message is returned callback, messageId: {}, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                    message, replyCode, replyText, exchange, routingKey);

            String messageId = message.getMessageProperties().getMessageId();
        }
    };
}