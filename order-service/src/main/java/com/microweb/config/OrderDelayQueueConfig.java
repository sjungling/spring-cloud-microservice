package com.microweb.config;

import com.microweb.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderDelayQueueConfig {
    /**
     * 延遲消費(delay consumer)步驟
     * producer -> DLX(Dead Letter Exchange) -> 佇列過期後變為死信佇列(Dead Letter Queue)
     * -> 透過(x-dead-letter-exchange)設定的DLX轉發到實際消費佇列 -> 實際消費佇列 -> consumer。
     */

    /**
     * 建立一個延遲佇列
     * 超過佇列或訊息設置的延遲時間後 就會變成 Dead Letter Queue(死信佇列)
     */
    @Bean
    public Queue delayQueue() {
        Map<String, Object> params = new HashMap<>(2);
        // x-dead-letter-exchange: 定義 佇列變為死信佇列(dead letter)後要轉發到的DLX(Dead Letter Exchange)名稱
        params.put("x-dead-letter-exchange", RabbitMQConstants.DelayQueue.DELAY_PROCESS_EXCHANGE);


        //x -dead-letter-routing-key: 定義死信佇列(dead letter queue)在轉發時，攜帶的route-key名稱。

        params.put("x-dead-letter-routing-key", RabbitMQConstants.DelayQueue.DELAY_PROCESS_ROUTING_KEY);
        return new Queue(RabbitMQConstants.DelayQueue.DELAY_QUEUE, true, false, false, params);

        /**
         * (不彈性) 設置Queue的延遲時間
         *
         * (較彈性) 於每次發送訊息下設置延遲時間
         */
        //params.put("x-com.microweb.flashsale.message-ttl", 30 * 1000);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(deadLetterExchange()).with(RabbitMQConstants.DelayQueue.DELAY_ROUTING_KEY);
    }

    /**
     * 死信佇列(dead letter queue)和交換機的類型無關，不一定要是directExchange
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.DelayQueue.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange delayProcessExchange() {
        /**
         * name: exchange名稱
         * durable: 是否持久
         * autoDelete: 是否可自動刪除
         * Map: 可存放這個自定義的exchange的參數
         */
        return new DirectExchange(RabbitMQConstants.DelayQueue.DELAY_PROCESS_EXCHANGE, true, false);
    }

    //關閉訂單 (死信轉發後，實際處理的佇列)
    @Bean
    Queue orderCloseDelayProcessQueue() {
        return new Queue(RabbitMQConstants.DelayQueue.ORDER_CLOSE_DELAY_PROCESS_QUEUE);
    }

    @Bean
    public Binding orderCloseDelayProcessBinding() {
        return BindingBuilder.bind(orderCloseDelayProcessQueue()).to(delayProcessExchange()).with(RabbitMQConstants.DelayQueue.DELAY_PROCESS_ROUTING_KEY);
    }
}