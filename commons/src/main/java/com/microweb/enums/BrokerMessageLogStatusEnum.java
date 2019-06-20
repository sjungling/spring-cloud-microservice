package com.microweb.enums;

import lombok.Getter;

@Getter
public enum BrokerMessageLogStatusEnum {
    MESSAGE_SENDING(0, "發送中"),
    MESSAGE_SEND_SUCCESS(1, "發送成功"),
    MESSAGE_SEND_FAILURE(2, "發送失敗");

    private Integer code;

    private String message;

    BrokerMessageLogStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}