package com.microweb.service.imple;

import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.Brand;
import com.microweb.repository.BrandRepository;
import com.microweb.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImple implements BrandService {
    @Autowired
    BrandRepository brandRepository;

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Brand findById(Long brandId) throws NotFoundException {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundException(Brand.class, String.format("Brand with id %d does not exist", brandId)));
    }

    @Override
    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }
}