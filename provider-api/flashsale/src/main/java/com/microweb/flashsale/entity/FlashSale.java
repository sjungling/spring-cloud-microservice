package com.microweb.flashsale.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

//@Data
@Getter
@Setter
@ToString(exclude = {"flashSaleProductSkus"})
@Entity
@Table(name = "flashsales")
public class FlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    //private Date beginTime;

    //private Date endTime;

    @JsonIgnore
    @OneToMany(mappedBy = "flashSale", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE} /*CascadeType.ALL*/)
    private List<FlashSaleProductSku> flashSaleProductSkus /*= new ArrayList<FlashSaleProductSku>()*/;
}