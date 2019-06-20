package com.microweb.repository;

import com.microweb.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Modifying
    @Transactional
    @Query(value = "update orders set status = ?2 where id = ?1", nativeQuery = true)
    Integer updateStatus(Long orderId, int statusCode);
}