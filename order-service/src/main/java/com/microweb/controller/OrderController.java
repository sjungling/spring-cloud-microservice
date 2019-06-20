package com.microweb.controller;

import com.microweb.enums.OrderStatusEnum;
import com.microweb.exception.NotFoundException;
import com.microweb.order.entity.Order;
import com.microweb.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(description = "訂單管理")
@Slf4j
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @ApiOperation(value = "根據訂單編號查詢訂單", notes = "", httpMethod = "GET")
    @GetMapping(value = "/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Long orderId) throws NotFoundException {
        return new ResponseEntity<>(orderService.findById(orderId), HttpStatus.OK);
    }

    @ApiOperation(value = "建立一般訂單", notes = "測試", httpMethod = "POST")
    @PostMapping(value = "/orders")
    public ResponseEntity createOrder(@RequestParam(name = "skuId") Long skuId, @RequestParam(name = "quantity") Integer quantity) throws NotFoundException {
        boolean isSuccess = orderService.createOrder(skuId, quantity);
        if (isSuccess) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "建立搶購優惠訂單", notes = "", httpMethod = "POST")
    @PostMapping(value = "/orders/flashSaleOrder")
    public ResponseEntity createFlashSaleOrder(@RequestBody @Valid Order order) throws NotFoundException {
        boolean isSuccess = orderService.createFlashSaleOrder(order);
        if (isSuccess) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "未付款訂單超時關閉", httpMethod = "PUT")
    @PutMapping(path = "/orders/{orderId}/close")
    public ResponseEntity orderClose(@PathVariable("orderId") Long orderId) throws NotFoundException {

        orderService.updateStatus(orderId, OrderStatusEnum.CLOSED);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
    @GetMapping(value = "/orders/test")
    public void test() {
        Order order = new Order();

        order.setUserId(1L);
        order.setMessageId("1234");
        order.setOrderStatusEnum(OrderStatusEnum.UNPAY);

        System.out.println(order);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setSkuId(12345L);
        orderDetail.setQuantity(1); //限時搶購寫死只能買一筆

        System.out.println(orderDetail);
        order.setOrderDetailList(Arrays.asList(orderDetail));

        System.out.println(order);
        System.out.println(order.getOrderDetailList());
    }
    */
}