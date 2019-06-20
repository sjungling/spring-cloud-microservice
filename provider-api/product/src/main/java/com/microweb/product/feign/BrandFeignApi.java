package com.microweb.product.feign;

import com.microweb.constant.ServiceNameConstants;
import com.microweb.product.entity.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//import com.microweb.product.feign.hystrix.BrandFeignApiFallback;

@FeignClient(name = ServiceNameConstants.PRODUCT_SERVICE/*, fallback = BrandFeignApiFallback.class*/)
public interface BrandFeignApi {
    @GetMapping("/brands")
    List<Brand> getBrandByIdList(@RequestParam("brandIds") List<Long> brandIds);

    @GetMapping("/brands/{brandId}")
    Brand getBrandById(@PathVariable("brandId") Long brandId);
}