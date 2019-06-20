package com.microweb.controller;

import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.ProductSku;
import com.microweb.service.ProductSkuService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "商品品項管理")
@RestController
@Slf4j
public class ProductSkuController {
    @Autowired
    ProductSkuService productSkuService;

    /**
     * consumes: HTTP Header ContentType --> client請求格式 ex: ContentType = application/json
     * produces: HTTP Header Accept --> client 接受的格式 ex: Accept: *\/*
     */
    @GetMapping(value = "/productSkus")
    public ResponseEntity<List<ProductSku>> getAllProductSku() {
        List<ProductSku> productSkus = productSkuService.findAll();

        return new ResponseEntity<>(productSkus, HttpStatus.OK);
    }

    @GetMapping("/productSkus/productId/{productId}")
    public ResponseEntity<List<ProductSku>> getProductSkuByProductId(@PathVariable("productId") Long productId) {
        return new ResponseEntity<>(productSkuService.findByProductId(productId), HttpStatus.OK);
    }

    @GetMapping(value = "/productSkus/{skuId}")
    public ResponseEntity<ProductSku> getProductSkuBySkuId(@PathVariable("skuId") Long skuId) throws NotFoundException {
        return new ResponseEntity<>(productSkuService.findBySkuId(skuId), HttpStatus.OK);
    }

    @PostMapping(value = "/productSkus/reduceStockByList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reduceStockByList(@RequestBody List<Map<String, Object>> reduceStockList) throws NotFoundException {
        System.out.println(reduceStockList);
        productSkuService.reduceStockByList(reduceStockList);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}