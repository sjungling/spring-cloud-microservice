package com.microweb.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(exclude = {"order"})
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long skuId;

    private Integer quantity;

    private BigDecimal price;

    @JsonIgnore //必要 (會造成json recursive)
    @ManyToOne(fetch = FetchType.EAGER/*, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}*/)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "order_id")
    private Order order;
}