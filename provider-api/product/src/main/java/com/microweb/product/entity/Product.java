package com.microweb.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microweb.enums.ProductStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

//@Data
@Getter
@Setter
@ToString(exclude = {"productSkus"})
//@EqualsAndHashCode
@Accessors(chain = true) //  ex:Product product = new Product();   product.setId().setName()
@Entity
@Table(name = "products")
public class Product {
    /**
     * AUTO - 主鍵由程式控制 。
     * IDENTITY - 由資料庫自動生成。
     * SEQUENCE - 根據底層資料庫的序列來生成主鍵 。
     * TABLE - 使用一個特定的資料庫表來儲存主鍵。
     * Enerator -指定生成主鍵使用的生成器 。
     * System-uuid 代表使用系統生成的uuid進行配。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @Column(unique = true, nullable = false)
    private Long productId;
    */
    private String name;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    private ProductStatusEnum status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "brand_id")  // create a column name: brand_id
    private Brand brand;
    //private Long brandId;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY/*, orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}*/)
    private List<ProductSku> productSkus /*= new ArrayList<>()*/;

    //    private List<ProductSkuAttribute> productSkuAttributes = new ArrayList<>();
}