package com.microweb.order.message;

import lombok.Data;

@Data
public class OrderMessage {
    private Long orderId;

    private String messageId;
}