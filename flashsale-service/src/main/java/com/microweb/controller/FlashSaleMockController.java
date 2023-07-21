package com.microweb.controller;

import com.microweb.constant.RedisConstants;
import com.microweb.exception.NotFoundException;
import com.microweb.flashsale.entity.FlashSaleProductSku;
import com.microweb.mq.sender.RabbitMQSender;
import com.microweb.service.FlashSaleService;
import com.microweb.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "模擬限時優惠搶購")
@RestController
public class FlashSaleMockController {
    private static final int corePoolSize = Runtime.getRuntime().availableProcessors();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize,
            corePoolSize + 1,
            10L,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<Runnable>(1000)
    );

    @Autowired
    FlashSaleService flashSaleService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @ApiOperation(value = "MySQL 悲觀鎖")
    @PostMapping("/flashsales/mock/mysql/pessimismLock")
    public void handlePessimismLockUpdateInMySQL(@RequestParam(required = false, defaultValue = "50") int requestCount,
    @RequestParam Long skuId,
    @RequestParam(required = false, defaultValue = "5") int stock,
    @RequestParam(required = false, defaultValue = "1") int purchaseQuantity) {

        Date startTime = new Date();

        log.info("Mysql PessimismLock -> start:{}, requestCount:{}, skuId:{}, stock:{} ,quantity:{}", startTime, requestCount, skuId, stock, purchaseQuantity);

        final CountDownLatch countDownLatch = new CountDownLatch(requestCount);

        for (int i = 0; i < requestCount; i++) {
            final long userId = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int result = flashSaleService.executePessimisticLockInMySql(skuId, purchaseQuantity);
                    if (result > 0) {
                        log.info("UserId:[{}] 成功搶購, Thread:{}", userId, Thread.currentThread().getName());
                    } else {
                        log.info("UserId:[{}] 搶購失敗, Thread:{}", userId, Thread.currentThread().getName());
                    }

                    countDownLatch.countDown();
                }
            };

            executor.execute(runnable);
        }

        try {
            //Thread.sleep(5000);
            countDownLatch.await();

            log.info("搶購次數:{}次 , 總花費時間:[{}] ms", requestCount, new Date().getTime() - startTime.getTime());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "MySQL 排他鎖(悲觀鎖)", notes = "高併發下，等待時間較長")
    @PostMapping("/flashsales/mock/mysql/pessimismLock/selectForUpdate")
    public void handlePessimismLockSelectForUpdateInMySQL(@RequestParam(required = false, defaultValue = "50") int requestCount,
    @RequestParam Long skuId,
    @RequestParam(required = false, defaultValue = "5") int stock,
    @RequestParam(required = false, defaultValue = "1") int purchaseQuantity) {

        Date startTime = new Date();
        log.info("Mysql PessimismLockSelectForUpdate -> start:{}, requestCount:{}, skuId:{}, stock:{} ,quantity:{}", startTime, requestCount, skuId, stock, purchaseQuantity);

        final CountDownLatch countDownLatch = new CountDownLatch(requestCount);

        for (int i = 0; i < requestCount; i++) {
            final long userId = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    FlashSaleProductSku flashSaleProductSku = flashSaleService.findFlashSaleProductSkuBySkuId(skuId);

                    //透過主鍵達到行鎖效果
                    Integer result = flashSaleService.executePessimisticLock4UpdateInMySql(flashSaleProductSku.getId(), skuId, purchaseQuantity);
                    if (result > 0) {
                        log.info("UserId:[{}] 成功搶購, Thread:{}", userId, Thread.currentThread().getName());
                    } else {
                        log.info("UserId:[{}] 搶購失敗, Thread:{}", userId, Thread.currentThread().getName());
                    }

                    countDownLatch.countDown();
                }
            };

            executor.execute(runnable);
        }

        try {
            //Thread.sleep(5000);
            countDownLatch.await();

            log.info("搶購次數:{}次 , 總花費時間:[{}] ms", requestCount, new Date().getTime() - startTime.getTime());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "MySQL 樂觀鎖")
    @PostMapping("/flashsales/mock/mysql/optimisticLock")
    public void handleOptimisticLockInMySQL(@RequestParam(required = false, defaultValue = "50") int requestCount,
    @RequestParam Long skuId,
    @RequestParam(required = false, defaultValue = "5") int stock,
    @RequestParam(required = false, defaultValue = "1") int purchaseQuantity) {

        Date startTime = new Date();
        log.info("Mysql OptimisticLock -> start:{}, requestCount:{}, skuId:{}, stock:{} ,quantity:{}", startTime, requestCount, skuId, stock, purchaseQuantity);

        final CountDownLatch countDownLatch = new CountDownLatch(requestCount);

        for (int i = 0; i < requestCount; i++) {
            final long userId = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    FlashSaleProductSku flashSaleProductSku = flashSaleService.findFlashSaleProductSkuBySkuId(skuId);

                    Integer result = flashSaleService.executeOptimisticLockInMySql(skuId, purchaseQuantity, flashSaleProductSku.getVersion());
                    if (result > 0) {
                        log.info("UserId:[{}] 成功搶購, Thread:{}", userId, Thread.currentThread().getName());
                    } else {
                        log.info("UserId:[{}] 搶購失敗, Thread:{}", userId, Thread.currentThread().getName());
                    }

                    countDownLatch.countDown();
                }
            };

            executor.execute(runnable);
        }

        try {
            //Thread.sleep(5000);
            countDownLatch.await();

            log.info("搶購次數:{}次 , 總花費時間:[{}] ms", requestCount, new Date().getTime() - startTime.getTime());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "Redisson分散式鎖阻塞鎖", notes = "等待時間內，進行重試獲取鎖")
    @PostMapping("/flashsales/mock/distributed/redisson/tryLock")
    public void handleByRedissonTryLcok(@RequestParam(required = false, defaultValue = "50") int requestCount,
    @RequestParam Long skuId,
    @RequestParam(required = false, defaultValue = "5") int stock,
    @RequestParam(required = false, defaultValue = "1") int purchaseQuantity) throws InterruptedException, NotFoundException {

        Date startTime = new Date();

        log.info("Redisson TryLock -> start:{}, requestCount:{}, skuId:{}, stock:{} ,quantity:{}", startTime, requestCount, skuId, stock, purchaseQuantity);

        final CountDownLatch countDownLatch = new CountDownLatch(requestCount);

        for (int i = 0; i < requestCount; i++) {
            final long userId = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String result = flashSaleService.executeRedissonTryLock(skuId, purchaseQuantity);

                    log.info("UserId:[{}] Msg:{}", userId, result);

                    countDownLatch.countDown();
                }
            };

            executor.execute(runnable);
        }

        try {
            //Thread.sleep(5000);
            countDownLatch.await();

            Integer remainStock = flashSaleService.getStockBySkuId(skuId);
            log.info("剩餘庫存:{}", remainStock);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 處理高併發問題
     *
     * 1.商品庫存，保存在redis
     * 2.redis預減庫存量
     *
     * todo:
     * 1.分散式鎖
     * 2.訂單入庫異常需rollback redis
     *   利用MQ來寫入訂單, 訂單服務需監聽訊息 成功後在透過商品服務扣db庫存
     *   若兩者其中有失敗 -> 需要考慮到資料一致性問題
     */
    @ApiOperation(value = "Redis預減庫存量，透過RabbitMq非同步創建訂單", notes = "高併發下減輕DB壓力", httpMethod = "POST")
    @PostMapping(value = "/flashsales/mock/stockInRedisAndUseRabbitMq/{skuId}")
    public ResponseEntity<String> StockInRedisAndUseRabbitMq(@PathVariable Long skuId) throws NotFoundException {
        Integer stock = flashSaleService.getStockInRedis(skuId);

        int remainStock = (int) redisUtils.hdecr(RedisConstants.FLASHSALE_PRODUCT_STOCKS, RedisConstants.FLASHSALE_PRODUCT_PREFIX_SKU + skuId, 1);

        if (remainStock <= 0) {
            return new ResponseEntity<>("商品已賣完", HttpStatus.NOT_FOUND);
        }

        flashSaleService.createOrderUseRabbitMQ(skuId, 1L);

        //return ResponseEntity.ok("排隊中");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}