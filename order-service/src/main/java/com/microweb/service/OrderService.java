package com.microweb.service;

import com.microweb.enums.OrderStatusEnum;
import com.microweb.exception.NotFoundException;
import com.microweb.order.entity.Order;

public interface OrderService {
    Order findById(Long orderId) throws NotFoundException;

    boolean createOrder(Long skuId, Integer quantity) throws NotFoundException;

    boolean createFlashSaleOrder(Order order) throws NotFoundException;

    void updateStatus(Long orderId, OrderStatusEnum orderStatusEnum) throws NotFoundException;
}