package com.microweb.service.imple;

import com.microweb.enums.ProductStatusEnum;
import com.microweb.exception.NotFoundException;
import com.microweb.page.PageResult;
import com.microweb.product.entity.Product;
import com.microweb.product.entity.ProductSku;
import com.microweb.repository.ProductRepository;
import com.microweb.repository.ProductSkuRepository;
import com.microweb.request.ProductRequest;
import com.microweb.service.BrandService;
import com.microweb.service.ProductService;
import com.microweb.service.ProductSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImple implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSkuRepository productSkuRepository;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductSkuService productSkuService;

    @Override
    public List<Product> findAll() {
        log.debug("Getting all products");
        List<Product> products = productRepository.findAll();
        log.debug("Found {} products", products.size());
        log.trace("Products: {}", products);

        return products;
    }

    @Override
    public PageResult<Product> findAllByPageAndCondition(String name, Long brandId, /*Long createAt, Long updateAt, */Pageable pageable) {

        Page<Product> products = productRepository.findAll(new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();

                if (StringUtils.isNotEmpty(name)) {
                    predicates.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
                }
                /*
                if (createAt != null && createAt != 0) {
                    predicates.add(criteriaBuilder.ge(root.get("startAt").as(Long.class), createAt));
                }

                if (updateAt != null && updateAt != 0) {
                    predicates.add(criteriaBuilder.ge(root.get("endAt").as(Long.class), updateAt));
                }
                */

                predicates.add(criteriaBuilder.equal(root.get("deleted").as(Boolean.class), false));

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        }, pageable);

        PageResult pageResult = new PageResult();
        pageResult.setTotal(products.getTotalElements());
        pageResult.setTotalPage(products.getTotalPages());
        pageResult.setRows(products.getContent());

        return pageResult;
    }

    @Override
    public Product findById(Long productId) throws NotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, String.format("Product with id %d does not exist", productId)));
    }

    @Override
    public Product findById(Long productId, boolean withAttribute) {
        return null;
    }

    @Override
    public List<Product> findByIdList(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return null;
        }

        return productRepository.findByIdIn(productIds);
    }
/*
    @Override
    public List<ProductSku> getSkuByProductId(Long productId) {
        //Product
        Optional<Product> products = productRepository.findById(productId);

        //ProductSku
        List<ProductSku> productSku = productSkuRepository.findByProductId(productId);
    }
    */

    @Override
    @Transactional
    public void createProduct(ProductRequest productRequest) {
        log.debug("Creating product: {}", productRequest);
        Product product = productRepository.save(requestToProduct(productRequest, new Product()));
        log.debug("Product with id {} created", product.getId());

        saveOrUpdateProductSku(productRequest.getProductSkus(), product);
    }

    @Override
    public void update(ProductRequest productRequest, Long productId) throws NotFoundException {
        log.debug("Updating product: {} with id {}", productRequest, productId);
        Product product = findById(productId);
        List<ProductSku> productSkuToUpdate = productRequest.getProductSkus();

        //validate productSku
        List<Long> skuIds = product.getProductSkus().stream().map(productSku -> productSku.getSkuId()).collect(Collectors.toList());
        for (ProductSku sku : productSkuToUpdate) {
            if (sku.getSkuId() != null && !skuIds.contains(sku.getSkuId())) {
                throw new NotFoundException(ProductSku.class, String.format("ProductSku with skuId %d does not exists", sku.getSkuId()));
            }
        }

        Product productToUpdate = requestToProduct(productRequest, product);
        productRepository.save(productToUpdate);
        log.debug("Product with id {} was updated", product.getId());

        saveOrUpdateProductSku(productSkuToUpdate, product);
    }


    @Override
    public List<Product> findByCategoryId(long categoryId) {
        return null;
    }

    @Override
    public ProductSku findProductSkuBySkuId(Long skuId) {
        return null;
    }

    @Override
    public ProductSku findProductSkuBySkuId(Long skuId, boolean withAttributes) {
        return null;
    }

    @Override
    @Transactional
    public void delete(Long productId) throws NotFoundException {
        productRepository.delete(findById(productId));
    }

    private void saveOrUpdateProductSku(List<ProductSku> skuList, Product product) {
        log.debug("Updating or Saving ProductSku: {} from product id: {}", skuList, product.getId());

        //原ProductSku預設為全刪除
        List<Long> productSkuDeleteIds = productSkuService.findByProductId(product.getId())
                .stream().map(sku -> sku.getId()).collect(Collectors.toList());

        for (ProductSku productSku : skuList) {
            productSku.setProduct(product);

            //delete exists productSku data that are not updated
            if (productSku.getId() != null && productSkuDeleteIds.contains(productSku.getId())) {
                productSkuDeleteIds.remove(productSku.getId());
            }
        }

        if (productSkuDeleteIds.size() > 0) {
            productSkuRepository.deleteByIdIn(productSkuDeleteIds);
            log.debug("ProductSku id: {} was deleted", productSkuDeleteIds);

        }

        productSkuRepository.saveAll(skuList);
        log.debug("ProductSku with id: {} was updated", product.getId());
    }

    private Product requestToProduct(ProductRequest productRequest, Product product) {
        //複製屬性
        //BeanUtils.copyProperties(productRequest, product);
        try {
            product
                    .setName(productRequest.getName())
                    .setBrand(brandService.findById(productRequest.getBrandId()))
                    .setStatus(ProductStatusEnum.DRAFT);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
        }

        return product;
    }

}