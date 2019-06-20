package com.microweb.request;

import com.microweb.product.entity.ProductSku;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
//@Accessors(chain = true)
public class ProductRequest {

    /**
     * NotEmpty: 不能為null，且長度需大於0
     * NotNull: 不能為null,但可以為empty ex: ("", " ", "   ")
     * NotBlank: 只能判斷String不能為null，且長度需大於0
     */

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 40, message = "Name length must be between 2 and 40 characters")
    private String name;

    /*
    @NotNull(com.microweb.flashsale.message = "Brand must be defined")
    private Brand brand;
    */
    @NotNull(message = "BrandId cannot be null")
    private Long brandId;

    @NotNull(message = "ProductSku must be defined")
    private List<ProductSku> productSkus;
    /*
    @NotNull(com.microweb.flashsale.message = "ProductCategory must be defined")
    private ProductCategory productCategory;
    */
    /*
    @JsonProperty("fileRequest")
    @JsonDeserialize
    private FileRequest fileRequest;
    */
}