package com.microweb.mq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class BrokerMessageLog {
    private String _id;

    private String messageId;

    private String message;

    private Integer tryCount;

    private Integer status;

    private Date nextRetry;

    private Date createTime;

    private Date updateTime;
}