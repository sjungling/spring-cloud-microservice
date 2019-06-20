package com.microweb.controller;

import com.microweb.flashsale.entity.FlashSale;
import com.microweb.flashsale.entity.FlashSaleProductSku;
import com.microweb.service.FlashSaleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "限時優惠搶購")
@RestController
public class FlashSaleController {
    @Autowired
    FlashSaleService flashSaleService;

    @GetMapping(value = "/flashsales/productSku/{skuId}")
    public ResponseEntity<FlashSaleProductSku> getFlashSaleProductSkuBySkuId(@PathVariable("skuId") Long skuId) {
        return new ResponseEntity<>(flashSaleService.findFlashSaleProductSkuBySkuId(skuId), HttpStatus.OK);
    }

    @PostMapping(value = "/flashsales/productSku/reduceStockByList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reduceStockByList(@RequestBody List<Map<String, Object>> reduceStockList) {
        //flashsaleService.reduceStockByList(reduceStockList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value = "新增限時搶購", httpMethod = "POST")
    @PostMapping(value = "/flashsales")
    public ResponseEntity createFlashSale(@Valid @RequestBody FlashSale flashSale) {
        return new ResponseEntity(HttpStatus.CREATED);
    }
}