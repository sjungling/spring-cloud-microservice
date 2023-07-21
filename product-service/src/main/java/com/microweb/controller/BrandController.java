package com.microweb.controller;

import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.Brand;
import com.microweb.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "品牌管理")
@RestController
@Slf4j
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping(value = "/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Brand>> getAllBrand() {
        return new ResponseEntity<>(brandService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/brands/{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Brand> getBrandById(@ApiParam(value = "brandId") @PathVariable long brandId) throws NotFoundException {
        return new ResponseEntity<>(brandService.findById(brandId), HttpStatus.OK);
    }

    @PostMapping(value = "/brands", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createBrand(@Valid @RequestBody Brand brand) {
        brandService.createBrand(brand);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}