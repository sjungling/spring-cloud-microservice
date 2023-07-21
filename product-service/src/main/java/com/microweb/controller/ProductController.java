package com.microweb.controller;

import com.microweb.exception.AlreadyExistsException;
import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.Brand;
import com.microweb.product.entity.Product;
import com.microweb.product.entity.ProductSku;
import com.microweb.repository.ProductRepository;
import com.microweb.request.ProductRequest;
import com.microweb.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "商品管理")
@RestController
@Slf4j
//@RequestMapping
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;


    @ApiOperation(value = "查詢所有商品(不分頁)")
    @GetMapping(value = "/products")
    public ResponseEntity<List<Product>> getAll() {
        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
    }


    @GetMapping(value = "/products/test")
    public void test() {
        List<Product> products = productService.findAll();

        for (Product product : products) {
            System.out.println(product);
            for (ProductSku productSku : product.getProductSkus()) {
                System.out.println(productSku);
            }
        }
    }


    /*
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PageResult<Product>> getAllByPageAndCondition(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "createAt", required = false) Long createAt,
            @RequestParam(value = "updateAt", required = false) Long updateAt,
            @PageableDefault(value = 15, sort = {"createAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResult<Product> products = productService.findAllByPageAndCondition()

    }
    */


    @PostMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createProduct(@Valid @RequestBody ProductRequest productRequest) throws AlreadyExistsException {
        log.debug("create product: " + productRequest);

        productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("/products/{productId}")
    public ResponseEntity updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest productRequest) throws NotFoundException {

        //log.info("update product: " + productRequest);
        productService.update(productRequest, productId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/products/test")
    public ResponseEntity testProduct() throws AlreadyExistsException {
        Product product = new Product();

        Brand brand = new Brand();
        brand.setId(3L);

        ProductSku productSku1 = new ProductSku();
        productSku1.setStock(10);
        productSku1.setPrice(new BigDecimal(Double.toString(20.3)));
        productSku1.setProduct(product);

        ProductSku productSku2 = new ProductSku();
        productSku2.setStock(3);
        productSku2.setPrice(new BigDecimal(Double.toString(76.1)));
        productSku2.setProduct(product);

        List<ProductSku> list = new ArrayList<>();
        list.add(productSku1);
        list.add(productSku2);

        product.setName("愛瘋修");
        product.setBrand(brand);
        product.setProductSkus(list);


        log.info("product:", product);
        productRepository.save(product);

        //Product product = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }






    /*
    @ApiOperation(httpMethod = "PUT", value = "商品下架")
    @PutMapping(value = "/offShelves/{productId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity offShelves(@ApiParam(value = "Product ID") @PathVariable(value = "productId") long productId) {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    */

    /*
    @ApiOperation(httpMethod = "DELETE", value = "根據商品ID刪除資料")
    @DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteProduct(@ApiParam(value = "productId") @PathVariable(value = "productId") long productId) throws NotFoundException {
        productService.delete(productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    */
}