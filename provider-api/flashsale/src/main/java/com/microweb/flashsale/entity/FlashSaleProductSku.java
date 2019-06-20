package com.microweb.flashsale.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

//@Data
@Getter
@Setter
@ToString(exclude = {"flashSale"})
@Entity
@Table(name = "flashsale_product_skus")
public class FlashSaleProductSku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String name; //商品名

    private Long productId;

    private Long skuId;

    private Integer stock;

    private BigDecimal price;

    private Integer version;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    // foreign key column name
    @JoinColumn(name = "flashSaleId"/*"flash_sale_id"*/, referencedColumnName = "id"/*, nullable = false*/)
    private FlashSale flashSale;
}