package com.microweb.product.feign;

import com.microweb.constant.ServiceNameConstants;
import com.microweb.product.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = ServiceNameConstants.PRODUCT_SERVICE/*, fallback = ProductFeignApiFallback.class*/)
public interface ProductFeignApi {
    @GetMapping(value = "/products")
    List<Product> getAll();

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
    Product getProductById(@PathVariable("productId") Long productId);
}