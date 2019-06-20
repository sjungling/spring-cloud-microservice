package com.microweb.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    UNPAY(0, "待付款"),
    PAID(1, "已付款"),
    CANCELLED(2, "已取消"),
    STOCK_SHORTAGE(3, "庫存不足"),
    CLOSED(4, "已關閉");

    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}