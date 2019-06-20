package com.microweb.order.entity;

import com.microweb.enums.OrderStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"orderDetailList"})
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String messageId; //rabbitMQ ID

    private BigDecimal totalPrice;

    private OrderStatusEnum status;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY/*, orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}*/)
    private List<OrderDetail> orderDetailList;
}