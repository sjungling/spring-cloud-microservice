package com.microweb.service.imple;

import com.microweb.constant.RabbitMQConstants;
import com.microweb.enums.BrokerMessageLogStatusEnum;
import com.microweb.enums.OrderStatusEnum;
import com.microweb.exception.NotFoundException;
import com.microweb.flashsale.entity.FlashSaleProductSku;
import com.microweb.flashsale.feign.FlashSaleFeignApi;
import com.microweb.mq.entity.BrokerMessageLog;
import com.microweb.mq.sender.RabbitMQDelaySender;
import com.microweb.order.entity.Order;
import com.microweb.order.entity.OrderDetail;
import com.microweb.order.message.OrderMessage;
import com.microweb.product.entity.ProductSku;
import com.microweb.product.feign.ProductSkuFeignApi;
import com.microweb.repository.OrderDetailRepository;
import com.microweb.repository.OrderRepository;
import com.microweb.service.OrderService;
import com.microweb.utils.BrokerMessageLogUtils;
import com.microweb.utils.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImple implements OrderService {
    @Autowired
    private BrokerMessageLogUtils brokerMessageLogUtils;

    @Autowired
    private RabbitMQDelaySender rabbitMQDelaySender;

    @Autowired
    private ProductSkuFeignApi productSkuFeignApi;

    @Autowired
    private FlashSaleFeignApi flashSaleFeignApi;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public Order findById(Long orderId) throws NotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(Order.class, String.format("Order with id %d does not exist", orderId)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Long skuId, Integer quantity) {
        String messageId = UUID.randomUUID().toString()/*System.currentTimeMillis() + "$" + UUID.randomUUID().toString()*/;

        ProductSku productSku = productSkuFeignApi.getProductSkuBySkuId(skuId);

        Order order = new Order();
        order.setUserId(123L);
        order.setMessageId(messageId);
        order.setStatus(OrderStatusEnum.UNPAY);

        List<OrderDetail> orderDetailList = new ArrayList<>();

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setPrice(productSku.getPrice());
        orderDetail.setSkuId(skuId);
        orderDetail.setQuantity(quantity);

        orderDetailList.add(orderDetail);

        System.out.println(order);
        System.out.println(orderDetail);

        boolean isEnough = checkProductStock(orderDetailList);


        if (isEnough) {
            order.setTotalPrice(getOrderTotalPrice(orderDetailList));
            Order orderSave = orderRepository.save(order);

            orderDetailList.forEach(oderDetail -> {
                oderDetail.setOrder(orderSave);
            });

            List<OrderDetail> orderDetailSave = orderDetailRepository.saveAll(orderDetailList);

            List<Map<String, Object>> reduceStockList = new ArrayList<>();

            for (OrderDetail detail : orderDetailSave) {
                Map<String, Object> map = new HashMap<>();
                //map.put("orderId", orderSave.getId());
                map.put("skuId", detail.getSkuId());
                map.put("quantity", detail.getQuantity());

                reduceStockList.add(map);


            }

            productSkuFeignApi.reduceStockByList(reduceStockList);

            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setMessageId(messageId);
            orderMessage.setOrderId(orderSave.getId());

            String message = FastJsonUtils.parseToString(orderMessage);
            /**
             * 建立本地訊息紀錄
             */
            BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
            brokerMessageLog
                    .setMessageId(messageId)
                    .setMessage(message)
                    .setStatus(BrokerMessageLogStatusEnum.MESSAGE_SENDING.getCode())
                    .setTryCount(0)
                    .setNextRetry(new Date(new Date().getTime() + RabbitMQConstants.RETRY_TIMEOUT)) //訊息未確認的超時時間，若時間到時則會重新發送訊息
                    //.setNextRetry(DateUtils.addMinutes(new Date(),RabbitMQConstants.RETRY_TIMEOUT))
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());

            brokerMessageLogUtils.save(brokerMessageLog);

            rabbitMQDelaySender.orderClose(message);

            return true;
        }

        return false;
    }

    @Override
    public boolean createFlashSaleOrder(Order order) throws NotFoundException {
        List<OrderDetail> orderDetailList = order.getOrderDetailList();

        boolean isEnough = checkFlashSaleProductStock(orderDetailList);
        if (isEnough) {
            order.setTotalPrice(getOrderTotalPrice(orderDetailList));
            Order orderSave = orderRepository.save(order);

            orderDetailList.forEach(oderDetail -> {
                oderDetail.setOrder(orderSave);
            });

            List<OrderDetail> orderDetailSave = orderDetailRepository.saveAll(orderDetailList);

            //扣庫存 [{"skuId":123,"quantity":1},{"skuId":234,"quantity":1}]
            List<Map<String, Object>> reduceStockList = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetailSave) {
                Map<String, Object> map = new HashMap<>();
                //map.put("orderId", orderSave.getId());
                map.put("skuId", orderDetail.getSkuId());
                map.put("quantity", orderDetail.getQuantity());

                reduceStockList.add(map);
            }

            System.out.println(reduceStockList);
            System.out.println(FastJsonUtils.parseToString(reduceStockList));

            productSkuFeignApi.reduceStockByList(reduceStockList);

            return true;
        }

        return false;
    }

    public void updateStatus(Long orderId, OrderStatusEnum orderStatusEnum) throws NotFoundException {
        Order order = findById(orderId);
        Integer result = orderRepository.updateStatus(orderId, orderStatusEnum.getCode());
        System.out.println(result);
    }

    private boolean checkFlashSaleProductStock(List<OrderDetail> orderDetail) {
        boolean isEnough = true;
        if (orderDetail.size() > 0) {
            for (OrderDetail detail : orderDetail) {
                FlashSaleProductSku sku = flashSaleFeignApi.getFlashSaleProductSkuBySkuId(detail.getSkuId());
                if (sku.getStock() - detail.getQuantity() < 0) {
                    isEnough = false;
                }
            }
        }

        return isEnough;
    }

    private boolean checkProductStock(List<OrderDetail> orderDetail) {
        boolean isEnough = true;
        if (orderDetail.size() > 0) {
            for (OrderDetail detail : orderDetail) {
                ProductSku productSku = productSkuFeignApi.getProductSkuBySkuId(detail.getSkuId());
                if (productSku.getStock() - detail.getQuantity() < 0) {
                    isEnough = false;
                }
            }
        }

        return isEnough;
    }

    /**
     * 注意！ 運算數字皆需轉換成String
     */
    private BigDecimal getOrderTotalPrice(List<OrderDetail> orderDetailList) {
        BigDecimal totalPrice = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            //multiply:乘法運算 --> 被乘數.multiply(乘數)
            BigDecimal orderDetailTotalPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity().toString()));
            totalPrice = totalPrice.add(orderDetailTotalPrice);
        }

        return totalPrice;
    }
}