package com.microweb.order.feign;

import com.microweb.constant.ServiceNameConstants;
import com.microweb.order.entity.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = ServiceNameConstants.ORDER_SERVICE)
public interface OrderFeignApi {
    @PostMapping(value = "/orders/flashSaleOrder")
    ResponseEntity createFlashSaleOrder(Order order);

    @GetMapping(value = "/orders/{orderId}")
    Order getOrderById(Long orderId);

    @PutMapping(path = "/orders/{orderId}/close")
    ResponseEntity orderClose(Long orderId);
}