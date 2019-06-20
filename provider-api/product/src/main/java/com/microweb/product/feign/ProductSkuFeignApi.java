package com.microweb.product.feign;

import com.microweb.constant.ServiceNameConstants;
import com.microweb.product.entity.ProductSku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = ServiceNameConstants.PRODUCT_SERVICE)
public interface ProductSkuFeignApi {
    @GetMapping("/productSkus")
    List<ProductSku> getAllProductSku();

    @GetMapping("/productSkus/productId/{productId}")
    List<ProductSku> getProductSkuByProductId(@PathVariable("productId") Long productId);

    @GetMapping("/productSkus/{skuId}")
    ProductSku getProductSkuBySkuId(@PathVariable("skuId") Long skuId);

    @PostMapping(value = "/productSkus/reduceStockByList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reduceStockByList(@RequestBody List<Map<String, Object>> reduceStockList);
}