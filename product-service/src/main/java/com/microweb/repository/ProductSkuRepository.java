package com.microweb.repository;

import com.microweb.product.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    List<ProductSku> findByProductId(Long productId);

    ProductSku findBySkuId(Long skuId);

    Optional<ProductSku> findByProductIdAndSkuId(Long productId, Long skuId);

    @Modifying
    @Query(value = "update product_skus set stock = stock -?2 where sku_id = ?1 and stock >= ?2", nativeQuery = true)
    void reduceStock(long skuId, int quantity);

    @Modifying
    @Transactional
    void deleteByIdIn(List<Long> ids);
}