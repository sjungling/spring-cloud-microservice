package com.microweb.flashsale.message;

import lombok.Data;

@Data
public class FlashSaleOrderMessage {
    private Long skuId;

    private Long userId;

    private String messageId;
}