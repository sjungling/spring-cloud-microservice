package com.microweb.product.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//@Data //Getter,Setter,ToString,EqualsAndHashCode,RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer orders;//顯示順序: 數字由小到大

    /*
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "category_id")
    private ProductCategory categorie;
    */
}