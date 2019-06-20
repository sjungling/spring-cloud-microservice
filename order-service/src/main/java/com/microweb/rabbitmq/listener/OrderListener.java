package com.microweb.rabbitmq.listener;

import com.microweb.constant.RabbitMQConstants;
import com.microweb.order.entity.Order;
import com.microweb.order.entity.OrderDetail;
import com.microweb.product.entity.ProductSku;
import com.microweb.enums.OrderStatusEnum;
import com.microweb.order.feign.OrderFeignApi;
import com.microweb.product.feign.ProductSkuFeignApi;
import com.microweb.flashsale.message.FlashSaleOrderMessage;
import com.microweb.order.message.OrderMessage;
import com.microweb.utils.FastJsonUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 法1 @RabbitListener("queue-name")，此方法需手動建立Queue (@Bean or Rabbitmq management)
 * <p>
 * 法2 透過 @RabbitListener(queuesToDeclare = @Queue("queue-name"))，指定Queue的名稱
 * <p>
 * 法3 自動創建Exchange和Queue的綁定
 * <p>
 * RabbitListener(bindings = @QueueBinding(
 * key = "route-key-name",
 * value = @Queue(value = "queue-name", durable = "true"),
 * exchange = @Exchange(name = "exchange-name", type = "topic")
 * ))
 */


//@RabbiitHandler使用時機為，當RabbitListener註解寫在Class時，可以指定由哪個function做訊息處理
@Component
@Slf4j
public class OrderListener {
    @Autowired
    private OrderFeignApi orderFeignApi;

    @Autowired
    private ProductSkuFeignApi productSkuFeignApi;

    @RabbitListener(queues = RabbitMQConstants.ORDER_CREATE_QUEUE)
    public void processHandleCreateFlashSaleOrder(@Payload String msg, Channel channel, Message message/*, @Header Map<String,Object> headers*/) throws IOException {
        //String receiverMsg = new String(com.microweb.flashsale.message.getBody(), "UTF-8");
        log.info("RabbitMQ - OrderConsumer createFlashSaleOrder, Msg: {}", msg);
        /**
         * 消息確認機制
         * Delivery Tag 用來標示投遞的訊息
         * 當 RabbitMQ 推送訓息給Consumer時，會附帶一個 Delivery Tag,
         * 以便 Consumer 可在訓息確認時告訴 RabbitMQ 是哪一條訊息被確認了
         */
        try {

            FlashSaleOrderMessage flashSaleOrderMessage = FastJsonUtils.parseToObject(msg, FlashSaleOrderMessage.class);

            String messageId = message.getMessageProperties().getMessageId();

            //TODO 查詢redis是否存在用戶訂單 -> 有:重複下單

            System.out.println(flashSaleOrderMessage);

            ProductSku productSku = productSkuFeignApi.getProductSkuBySkuId(flashSaleOrderMessage.getSkuId());

            //創建訂單資訊
            Order order = new Order();

            order.setUserId(flashSaleOrderMessage.getUserId());
            order.setMessageId(messageId);
            order.setStatus(OrderStatusEnum.UNPAY);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setPrice(productSku.getPrice());
            orderDetail.setSkuId(flashSaleOrderMessage.getSkuId());
            orderDetail.setQuantity(1); //限時搶購寫死只能買一筆

            //order.getOrderDetailList().add(orderDetail);
            order.setOrderDetailList(Arrays.asList(orderDetail));

            ResponseEntity responseEntity = orderFeignApi.createFlashSaleOrder(order);
            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                /**
                 * basicAck(deliveryTag, multiple):
                 * 1. 告訴broker 此條訊息已消費成功，可於佇列(Queue)中移除
                 * 2. multiple:true 時，表示一次性確認 delivery_tag 小於等於傳入的值的所有訊息
                 */
//                Map<String, Object> header = com.microweb.flashsale.message.getMessageProperties().getHeaders();
//                Long deliverTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

                //todo 需提供一個接口讓前端輪詢(websocket)，通知訂單已建立。
            }
        } catch (Exception e) {
            /**
             * basicNack(deliveryTag, multiple, requeue):
             * 1. 告訴broker 此條訊息消費失敗
             * 2. multiple:true 時，表示一次性確認 delivery_tag小於等於傳入的值的所有訊息
             * 3. requeue:true -> 重新放回queue ; false -> 移除
             * 4. 放到dead-lettered 佇列
             */
            e.printStackTrace();
            /*
            if (com.microweb.flashsale.message.getMessageProperties().getRedelivered()) {
                //訊息已經重新發送過，卻還是失敗，則丟棄此訊息
                log.error("訊息已重新發送仍失敗, messageId: {}", com.microweb.flashsale.message.getMessageProperties().getMessageId());

                //channel.basicReject(com.microweb.flashsale.message.getMessageProperties().getDeliveryTag(),false);
                channel.basicNack(com.microweb.flashsale.message.getMessageProperties().getDeliveryTag(), false, false);

            } else {
                log.error("訊息處理失敗，丟回佇列重新處理, messageId: {}", com.microweb.flashsale.message.getMessageProperties().getMessageId());
                //channel.basicReject(com.microweb.flashsale.message.getMessageProperties().getDeliveryTag(),true);
                channel.basicNack(com.microweb.flashsale.message.getMessageProperties().getDeliveryTag(), false, true);
            }
            */
        }
    }

    //延遲佇列，訂單超過時間未付款，關閉訂單
    @RabbitListener(queues = RabbitMQConstants.DelayQueue.ORDER_CLOSE_DELAY_PROCESS_QUEUE)
    public void processHandleOrderClose(@Payload String msg, Channel channel, Message message) {
        log.info("RabbitMQ - OrderConsumer DelayQueue:OrderClose, Msg: {}", msg);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("訂單成立已超過30分鐘，關閉狀態為未付款的訂單，目前時間:" + sdf.format(new Date()));

            OrderMessage orderMessage = FastJsonUtils.parseToObject(msg, OrderMessage.class);

            Order order = orderFeignApi.getOrderById(orderMessage.getOrderId());

            System.out.println(order);

            if (order.getStatus().getCode().equals(OrderStatusEnum.UNPAY.getCode())) {
                log.info("Order id: {}, status: {}", order.getId(), order.getStatus());

                orderFeignApi.orderClose(order.getId());
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}