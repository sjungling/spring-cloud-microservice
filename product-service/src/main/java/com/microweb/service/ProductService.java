package com.microweb.service;

import com.microweb.exception.NotFoundException;
import com.microweb.page.PageResult;
import com.microweb.product.entity.Product;
import com.microweb.product.entity.ProductSku;
import com.microweb.request.ProductRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    PageResult<Product> findAllByPageAndCondition(String name, Long brandId/*, Long startTime, Long endTime, */, Pageable pageable);

    //List<ProductSku> getSkuByProductId(Long productId);

    /**
     * Get product by itself primary key value
     */
    Product findById(Long productId) throws NotFoundException;

    /**
     * Get product by itself primary key value
     */
    Product findById(Long productId, boolean withAttribute) throws NotFoundException;

    /**
     * Get list of products by productIds
     */
    List<Product> findByIdList(List<Long> productIds);

    /**
     * Get the all products in category
     *
     * @param categoryId category id
     * @return list of products
     */
    List<Product> findByCategoryId(long categoryId);

    /**
     * Get product sku by itself skuid
     *
     * @param skuId given sku id
     * @return product sku
     */
    ProductSku findProductSkuBySkuId(Long skuId);

    /**
     * Get product sku by itself skuid
     *
     * @param skuId          given sku id
     * @param withAttributes with attributes
     * @return product sku
     */
    ProductSku findProductSkuBySkuId(Long skuId, boolean withAttributes);

    void createProduct(ProductRequest productRequest);

    void update(ProductRequest productRequest, Long productId) throws NotFoundException;

    void delete(Long productId) throws NotFoundException;
}