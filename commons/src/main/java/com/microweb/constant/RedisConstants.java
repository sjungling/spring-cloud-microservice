package com.microweb.constant;

public class RedisConstants {
    // redis lock
    public static final Integer LOCK_WAIT_TIME = 10;
    public static final Integer LOCK_TIMEOUT = 60;

    public static final String PRODUCT_STOCKS = "product_stocks";
    public static final String PRODUCT_PREFIX_SKU = "product_prefix_sku:";

    public static final String FLASHSALE_PRODUCT_STOCKS = "flashsale_product_stocks";
    public static final String FLASHSALE_PRODUCT_PREFIX_SKU = "flashsale_product_prefix_sku:";
}