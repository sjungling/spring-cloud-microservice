package com.microweb.service;

import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> findAll();

    Brand findById(Long brandId) throws NotFoundException;

    Brand createBrand(Brand brand);
}