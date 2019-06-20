package com.microweb.repository;

import com.microweb.flashsale.entity.FlashSaleProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface FlashSaleProductSkuRepository extends JpaRepository<FlashSaleProductSku, Long> {
    FlashSaleProductSku findBySkuId(Long skuId);

    @Transactional
    @Modifying
    @Query(value = "update flashsale_product_skus set stock = stock -?2 where sku_id = ?1", nativeQuery = true)
    Integer reduceStockBySkuIdAndQuantity(Long skuId, Integer quantity);

    @Transactional
    @Modifying
    @Query(value = "update flashsale_product_skus set stock = stock -?2 where sku_id = ?1 and stock >= ?2", nativeQuery = true)
    Integer reduceStockBySkuIdAndStockMoreThanQuantity(Long skuId, Integer quantity);

    @Transactional
    @Modifying
    @Query(value = "update flashsale_product_skus set stock = stock -?2 where sku_id = ?1 and stock >= ?2", nativeQuery = true)
    int updateStockByPessimisticLock(Long skuId, Integer quantity);

    @Transactional
    @Modifying
    @Query(value = "update flashsale_product_skus set stock = stock -?2, version = version +1 where sku_id = ?1 and version = ?3", nativeQuery = true)
    int updateStockByOptimisticLock(Long skuId, Integer quantity, Integer version);

    @Query(value = "select stock from flashsale_product_skus where sku_id = ?1", nativeQuery = true)
    int getStockBySkuId(Long skuId);

    @Modifying
    @Query(value = "select stock from flashsale_product_skus where id = ?1 for update", nativeQuery = true)
    Integer getStockByIdAndSelect4UpdateLock(Long flashSaleProductSkuId);
}