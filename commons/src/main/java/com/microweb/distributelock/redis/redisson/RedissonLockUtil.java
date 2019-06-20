package com.microweb.distributelock.redis.redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockUtil {

    private RedissonClient redissonClient;

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * ReentrantLock distributed usage
     * <p>
     * Redisson 的RLock 支持可重入(ReentrantLock)
     *
     * @param key lock name
     * @return RLock Object
     */
    public RLock lock(String key) {
        RLock rLock = redissonClient.getLock(key);
        rLock.lock();

        return rLock;
    }

    /**
     * (非阻塞式鎖)
     * Distributed lock with timeout.
     * 1. If the lock is not available then the thread will be blocked until acquire the lock.
     * 2. The lock will be hold until unlock is invoked or until timeout seconds have passed.
     *
     * @param key     lock name
     * @param timeout the maximum time to hold the lock after granting(釋放) it.
     * @return RLock Object
     */
    public RLock lock(String key, int timeout) {
        RLock rLock = redissonClient.getLock(key);
        rLock.lock(timeout, TimeUnit.SECONDS);

        return rLock;
    }

    /**
     * (非阻塞式鎖)
     *
     * @param key      lock name
     * @param timeout  the maximum time to hold the lock after granting(釋放) it.
     * @param timeUnit the time unit of timeout. (超時單位)
     * @return RLock Object
     */
    public RLock lock(String key, int timeout, TimeUnit timeUnit) {
        RLock rLock = redissonClient.getLock(key);
        rLock.lock(timeout, timeUnit);

        return rLock;
    }

    /**
     * (阻塞式鎖)
     * Returns true as soon as the lock is acquired.
     * <p>
     * 1. If the lock is obtained by another thread of another process in distributed system,
     * will wait for waitTime seconds. And then give up with returns false.
     * <p>
     * 2. The lock is hold until unlock is invoked or until leaseTime has passed.
     *
     * @param key       lock name
     * @param waitTime  the maximum time to acquire the lock
     * @param leaseTime lease time
     * @return true if lock has been successfully acquired or others are false.
     */
    public boolean tryLock(String key, int waitTime, int leaseTime) {
        RLock rLock = redissonClient.getLock(key);
        try {

            return rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * (阻塞式鎖)
     *
     * @param key       lock name
     * @param waitTime  the maximum time to acquire the lock
     * @param leaseTime lease time
     * @param timeUnit  the time unit of timeout. (超時單位)
     * @return true if lock has been successfully acquired or others are false.
     */
    public boolean tryLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit) {
        RLock rLock = redissonClient.getLock(key);
        try {
            return rLock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * unlock distributed lock
     *
     * @param key lock name
     */
    public void unlock(String key) {
        RLock rLock = redissonClient.getLock(key);

        rLock.unlock();
    }

    /**
     * unlock distributed lock
     *
     * @param rLock lock object
     */
    public void unlock(RLock rLock) {
        rLock.unlock();
    }
}