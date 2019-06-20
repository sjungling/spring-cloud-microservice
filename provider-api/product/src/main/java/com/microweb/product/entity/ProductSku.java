package com.microweb.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

//@Builder
//@Data
@Getter
@Setter
@ToString(exclude = {"product", "productSkuAttributes"})
//@EqualsAndHashCode
@Entity
@Table(name = "product_skus")
public class ProductSku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long skuId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "1", message = "Price cannot be less than $1")
    @DecimalMax(value = "100", message = "Price cannot be more than $100")
    private BigDecimal price;//BigDecimal price;

    @NotNull(message = "Stock cannot be null")
    @DecimalMin(value = "0", message = "Stock cannot be less than $0")
    private Integer stock;

    @Column(nullable = true, name = "barcode", length = 50)
    private String barcode; //商品條碼

    @JsonIgnore //必要 (會造成json recursive)
    @ManyToOne(fetch = FetchType.EAGER/*, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}*/)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_id")
    private Product product;
    //private Long productId;

    /*
    @Transient
    private List<ProductSkuAttribute> productSkuAttributes;
    */

    /*
    @JsonIgnore
    @Where(clause = "is_sku = 1")
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<ProductSkuAttribute> getProductSkuAttributes() {
        return productSkuAttributes;
    }
    */
}