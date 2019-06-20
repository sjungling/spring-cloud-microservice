package com.microweb.repository;

import com.microweb.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "select p.*,productsku.* from products as p left join product_skus as productsku on p.id = productsku.product_id", nativeQuery = true)
    List<Product> findAllWithProductSku();

    // Product getProductById(Long productId);

    List<Product> findByIdIn(List<Long> productId);

    Page<Product> findAll(Specification<Product> specification, Pageable pageable);
}