package com.microweb.service;

import com.microweb.exception.NotFoundException;
import com.microweb.flashsale.entity.FlashSale;
import com.microweb.flashsale.entity.FlashSaleProductSku;

public interface FlashSaleService {
    FlashSale initMockData(Integer stock);

    void createOrderUseRabbitMQ(Long skuId, Long userId);

    Integer getStockBySkuId(Long skuId);

    Integer getStockInRedis(Long skuId) throws NotFoundException;

    FlashSaleProductSku findFlashSaleProductSkuBySkuId(Long skuId);

    //void reduceStockByList(List<Map<String, Object>> reduceStockByList);

    int executePessimisticLockInMySql(Long skuId, Integer quantity);

    Integer executePessimisticLock4UpdateInMySql(Long flashSaleProductId, Long flashSaleProductSkuId, Integer quantity);

    Integer executeOptimisticLockInMySql(Long skuId, Integer quantity, Integer version);

    String executeRedissonTryLock(Long skuId, Integer quantity);
}