package com.microweb.service.imple;

import com.microweb.exception.NotFoundException;
import com.microweb.product.entity.ProductSku;
import com.microweb.repository.ProductSkuRepository;
import com.microweb.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProductSkuServiceImple implements ProductSkuService {
    @Autowired
    private ProductSkuRepository productSkuRepository;

    @Override
    public List<ProductSku> findAll() {
        return productSkuRepository.findAll();
    }

    @Override
    public List<ProductSku> findByProductId(Long productId) {
        return productSkuRepository.findByProductId(productId);
    }

    @Override
    public ProductSku findBySkuId(Long skuId) throws NotFoundException {
        ProductSku productSku = productSkuRepository.findBySkuId(skuId);
        if (productSku == null) {
            throw new NotFoundException(ProductSku.class, String.format("ProductSku with skuId: %d not exist.", skuId));
        }

        return productSku;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reduceStockByList(List<Map<String, Object>> reduceStockList) throws NotFoundException {
        if (reduceStockList != null && reduceStockList.size() > 0) {
            for (Map<String, Object> map : reduceStockList) {
                //Long orderId = (Long) map.get("orderId");
                //Long skuId = ((Long) map.get("skuId")).longValue();

                Integer skuId = (Integer) map.get("skuId");
                Integer quantity = (Integer) map.get("quantity");

                //TODO 是否要判斷訂單狀態

                //查驗商品庫存
                ProductSku productSku = findBySkuId(skuId.longValue());
                if (productSku.getStock() - quantity >= 0) {
                    productSkuRepository.reduceStock(skuId.longValue(), quantity);
                }
            }
        }
    }
}