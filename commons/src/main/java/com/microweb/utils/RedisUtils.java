package com.microweb.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    public static final TimeUnit TIME_TO_HOURS = TimeUnit.HOURS;
    public static final TimeUnit TIME_TO_MINUTES = TimeUnit.MINUTES;
    public static final TimeUnit TIME_TO_SECONDS = TimeUnit.SECONDS;
    public static final TimeUnit TIME_TO_MILLISECONDS = TimeUnit.MILLISECONDS;

    /*
    @Autowired
    private final StringRedisTemplate stringRedisTemplate;
    */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object objVal) {
        redisTemplate.opsForValue().set(key, objVal);
    }

    /**
     * @param key
     * @param objectVal
     * @param timeout   if timeout lessThan or equalTo zero , timeout is indefinitely
     */
    public void set(String key, Object objectVal, long timeout) {
        try {
            if (timeout > 0) {

                redisTemplate.opsForValue().set(key, objectVal, timeout, TimeUnit.SECONDS);
            } else {
                set(key, objectVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public double incr(String key, double delta) {
        if (delta < 0) {
            throw new RuntimeException("RedisUtils incr delta must moreThan zero");
        }

        return redisTemplate.opsForValue().increment(key, delta);
    }

    public double decr(String key, double delta) {
        if (delta < 0) {
            throw new RuntimeException("RedisUtils decr delta must moreThan zero");
        }

        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * Expire the caching by specific Key
     *
     * @param key      String key
     * @param timeout  expire time (TTL)
     * @param timeUnit
     */
    public void expire(String key, final long timeout, final TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * Get expiring time (TTL)
     *
     * @param key String key
     * @return Long expire time
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * Set the {@code value} of a hash {@code hashKey}.
     */
    public void hset(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * Get values for given {@code hashKey} from hash at {@code key}.
     */
    public Object hget(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * Delete given hash {@code hashKeys}.
     */

    public void hdel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * Determine if given hash {@code hashKey} exists.
     */
    public boolean hHashKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Increment {@code value} of a hash {@code hashKey} by the given {@code delta}.
     */
    public double hincr(String key, Object hashKey, double delta) {
        if (delta < 0) {
            throw new RuntimeException("Redis hincr delta must moreThan zero");
        }
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    public double hdecr(String key, Object hashKey, double delta) {
        if (delta < 0) {
            throw new RuntimeException("Redis hdecr delta must moreThan zero");
        }
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }
}