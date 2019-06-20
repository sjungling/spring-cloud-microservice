package com.microweb.repository;

import com.microweb.flashsale.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {
}