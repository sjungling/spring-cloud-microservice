package com.microweb.service;

import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.ProductSku;

import java.util.List;
import java.util.Map;

public interface ProductSkuService {
    List<ProductSku> findAll();

    List<ProductSku> findByProductId(Long productId);

    ProductSku findBySkuId(Long skuId) throws NotFoundException;

    void reduceStockByList(List<Map<String, Object>> reduceStockList) throws NotFoundException;
}