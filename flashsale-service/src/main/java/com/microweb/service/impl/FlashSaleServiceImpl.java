package com.microweb.service.impl;

import com.microweb.constant.RabbitMQConstants;
import com.microweb.constant.RedisConstants;
import com.microweb.distributelock.redis.redisson.RedissonLockUtil;
import com.microweb.enums.BrokerMessageLogStatusEnum;
import com.microweb.exception.NotFoundException;
import com.microweb.flashsale.entity.FlashSale;
import com.microweb.flashsale.entity.FlashSaleProductSku;
import com.microweb.flashsale.message.FlashSaleOrderMessage;
import com.microweb.mq.entity.BrokerMessageLog;
import com.microweb.mq.sender.RabbitMQSender;
import com.microweb.product.entity.Product;
import com.microweb.product.entity.ProductSku;
import com.microweb.product.feign.ProductFeignApi;
import com.microweb.product.feign.ProductSkuFeignApi;
import com.microweb.repository.FlashSaleProductSkuRepository;
import com.microweb.repository.FlashSaleRepository;
import com.microweb.service.FlashSaleService;
import com.microweb.utils.BrokerMessageLogUtils;
import com.microweb.utils.FastJsonUtils;
import com.microweb.utils.RedisUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleServiceImpl implements FlashSaleService, InitializingBean {
    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private FlashSaleProductSkuRepository flashSaleProductSkuRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedissonLockUtil redissonLockUtil;

    @Autowired
    private BrokerMessageLogUtils brokerMessageLogUtils;

    @Autowired
    private ProductFeignApi productFeignApi;

    @Autowired
    private ProductSkuFeignApi productSkuFeignApi;

    @Autowired
    RabbitMQSender rabbitMQSender;

    @Override
    public FlashSaleProductSku findFlashSaleProductSkuBySkuId(Long skuId) {
        return flashSaleProductSkuRepository.findBySkuId(skuId);
    }

    @Override
    public void createOrderUseRabbitMQ(Long skuId, Long userId) {
        String messageId = UUID.randomUUID().toString()/*System.currentTimeMillis() + "$" + UUID.randomUUID().toString()*/;

        FlashSaleOrderMessage flashSaleOrderMessage = new FlashSaleOrderMessage();
        flashSaleOrderMessage.setSkuId(skuId);
        flashSaleOrderMessage.setUserId(1L);
        flashSaleOrderMessage.setMessageId(messageId);

        /**
         * 建立本地訊息紀錄
         */
        BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
        brokerMessageLog
                .setMessageId(messageId)
                .setMessage(FastJsonUtils.parseToString(flashSaleOrderMessage))
                .setStatus(BrokerMessageLogStatusEnum.MESSAGE_SENDING.getCode())
                .setTryCount(0)
                .setNextRetry(new Date(new Date().getTime() + RabbitMQConstants.RETRY_TIMEOUT)) //訊息未確認的超時時間，若時間到時則會重新發送訊息
                //.setNextRetry(DateUtils.addMinutes(new Date(),RabbitMQConstants.RETRY_TIMEOUT))
                .setCreateTime(new Date())
                .setUpdateTime(new Date());


        brokerMessageLogUtils.save(brokerMessageLog);

        rabbitMQSender.createFlashSaleOrder(FastJsonUtils.parseToString(flashSaleOrderMessage));
    }

    public Integer getStockBySkuId(Long skuId) {
        return flashSaleProductSkuRepository.getStockBySkuId(skuId);
    }

    @Override
    public Integer getStockInRedis(Long skuId) throws NotFoundException {
        if (redisUtils.hHashKey(RedisConstants.FLASHSALE_PRODUCT_STOCKS, RedisConstants.FLASHSALE_PRODUCT_PREFIX_SKU + skuId)) {
            Integer stock = Integer.parseInt(redisUtils.hget(RedisConstants.FLASHSALE_PRODUCT_STOCKS,
                    RedisConstants.FLASHSALE_PRODUCT_PREFIX_SKU + skuId).toString());
            return stock;
        }

        FlashSaleProductSku flashSaleProductSku = findFlashSaleProductSkuBySkuId(skuId);
        if (flashSaleProductSku == null) {
            throw new NotFoundException(FlashSaleProductSku.class, String.format("FlashSaleProductSku with id:%d not exist", skuId));
        }
        //存於redis
        redisUtils.hset(RedisConstants.FLASHSALE_PRODUCT_STOCKS,
                RedisConstants.FLASHSALE_PRODUCT_PREFIX_SKU + flashSaleProductSku.getSkuId(),
                flashSaleProductSku.getStock());

        return flashSaleProductSku.getStock();
    }


    @Override
    public int executePessimisticLockInMySql(Long skuId, Integer quantity) {
        return flashSaleProductSkuRepository.updateStockByPessimisticLock(skuId, quantity);
    }

    @Override
    public Integer executePessimisticLock4UpdateInMySql(Long flashSaleProductId, Long flashSaleProductSkuId, Integer quantity) {
        Integer remainStock = flashSaleProductSkuRepository.getStockByIdAndSelect4UpdateLock(flashSaleProductId);
        if (remainStock > 0) {
            return flashSaleProductSkuRepository.reduceStockBySkuIdAndQuantity(flashSaleProductSkuId, quantity);
        } else {
            return 0;
        }
    }

    @Override
    public Integer executeOptimisticLockInMySql(Long skuId, Integer quantity, Integer version) {
        return flashSaleProductSkuRepository.updateStockByOptimisticLock(skuId, quantity, version);
    }

    @Override
    public String executeRedissonTryLock(Long skuId, Integer quantity) {
        boolean isLock = false;
        String response = "";
        try {
            //阻塞式獲取鎖，等待獲取鎖最多10秒，30秒後釋放鎖(預設時間單位為秒) (設定的時間需依功能需求變動)
            isLock = redissonLockUtil.tryLock(skuId + "", 10, 30, TimeUnit.SECONDS);

            if (isLock) {
                FlashSaleProductSku flashSaleProductSku = flashSaleProductSkuRepository.findBySkuId(skuId);
                if (flashSaleProductSku != null) {
                    //1. check stock num
                    if (flashSaleProductSku.getStock() > 0) {
                        int result = flashSaleProductSkuRepository.reduceStockBySkuIdAndQuantity(skuId, quantity);

                        if (result > 0) {
                            response = "Purchase Successfully.";
                        } else {
                            response = "Purchase Fail.";
                        }
                    } else {
                        response = "Product Has Been Sold Out.";
                    }
                } else {
                    response = "Product Not Found.";
                }

                Thread.sleep(3000);

                redissonLockUtil.unlock(skuId + "");
                System.out.println("Thread:" + Thread.currentThread().getName() + "釋放鎖");
            } else {
                response = "Too Many Request.";
                System.out.println("Thread:" + Thread.currentThread().getName() + "等待鎖");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redissonLockUtil.unlock(skuId + "");
        }

        return response;
    }

/*
    @Transactional(rollbackFor = Exception.class)
    public void reduceStockByList(List<Map<String, Object>> reduceStockList) {
        if (reduceStockList != null && reduceStockList.size() > 0) {
            for (Map<String, Object> map : reduceStockList) {
                //Long orderId = (Long) map.get("orderId");
                //Long skuId = ((Long) map.get("skuId")).longValue();

                Integer skuId = (Integer) map.get("skuId");
                Integer quantity = (Integer) map.get("quantity");

                //TODO 是否要判斷訂單狀態

                //查驗搶購優惠商品庫存
                ProductSku productSku = findBySkuId(skuId.longValue());
                if (productSku.getStock() - quantity >= 0) {
                    productSkuRepository.reduceStock(skuId.longValue(), quantity);
                }
            }
        }
    }
    */

    @Override
    public FlashSale initMockData(Integer stock) {
//        FlashSaleProductSku flashSaleProductSku = new FlashSaleProductSku();
//        flashSaleProductSku.setId(2L);
//
//        flashSaleProductSkuRepository.delete(flashSaleProductSku);

        FlashSale flashSale = new FlashSale();
        flashSale.setId(1L);
        flashSale.setTitle("限時商品搶購");
        flashSaleRepository.delete(flashSale);
        /*
        FlashSaleProductSku flashSaleProductSku1 = new FlashSaleProductSku();
        flashSaleProductSku1.setStock(stock);
        flashSaleProductSku1.setProductId(1L);
        flashSaleProductSku1.setFlashSale(flashSale);

        FlashSaleProductSku flashSaleProductSku2 = new FlashSaleProductSku();
        flashSaleProductSku2.setStock(stock);
        flashSaleProductSku2.setProductId(2L);
        flashSaleProductSku2.setFlashSale(flashSale);

        System.out.println(flashSale);
        System.out.println(flashSaleProductSku1);

        //flashSaleProductSkuRepository.saveAll(Arrays.asList(flashSaleProductSku1, flashSaleProductSku2));


        /*
//        getFlashSaleProductSkus :需為new ArrayList<FlashSaleProductSkus>() ;
//        flashSale.getFlashSaleProductSkus().add(flashSaleProductSku1);
//        flashSale.getFlashSaleProductSkus().add(flashSaleProductSku2);


        List<FlashSaleProductSku> flashSaleProductSkus = new ArrayList<>();

        flashSaleProductSkus.add(flashSaleProductSku1);
        flashSaleProductSkus.add(flashSaleProductSku2);

        System.out.println(flashSaleProductSkus);

        flashSale.setFlashSaleProductSkus(flashSaleProductSkus);

        System.out.println(flashSale);

        flashSaleRepository.save(flashSale);
        */
        return flashSale;

    }


    @Override
    public void afterPropertiesSet() {
        flashSaleRepository.deleteAll();
        flashSaleProductSkuRepository.deleteAll();

        List<Product> products = productFeignApi.getAll();

        FlashSale flashSale = new FlashSale();
        flashSale.setTitle("搶購優惠活動測試");

        List<FlashSaleProductSku> flashSaleProductSkuList = new ArrayList<>();
        for (Product product : products) {
            for (ProductSku productSku : product.getProductSkus()) {
                FlashSaleProductSku flashSaleProductSku = new FlashSaleProductSku();

                flashSaleProductSku.setProductId(product.getId());
                flashSaleProductSku.setSkuId(productSku.getSkuId());
                flashSaleProductSku.setStock(productSku.getStock());
                flashSaleProductSku.setPrice(productSku.getPrice());

                flashSaleProductSkuList.add(flashSaleProductSku);
            }
        }

        flashSaleProductSkuList.forEach((flashSaleProductSku) -> flashSaleProductSku.setFlashSale(flashSale));

        flashSaleProductSkuRepository.saveAll(flashSaleProductSkuList);
    }
}