package com.microweb.flashsale.feign;

import com.microweb.constant.ServiceNameConstants;
import com.microweb.flashsale.entity.FlashSaleProductSku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(ServiceNameConstants.FLASHSALE_SERVICE)
public interface FlashSaleFeignApi {
    @GetMapping(value = "/flashsales/productSku/{skuId}")
    FlashSaleProductSku getFlashSaleProductSkuBySkuId(@PathVariable("skuId") Long skuId);

    @PostMapping(value = "/flashsales/productSku/reduceStockByList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    void reduceStockByList(@RequestBody List<Map<String, Object>> reduceStockList);
}