package com.seckill.smseckill.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: DistributeLock
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/12 14:16
 * @Version: 1.0
 */
@Component
public class DistributedLock {
    private static final String LOCK_KEY_PREFIX = "sm-seckill:lock:";
    private static final long LOCK_EXPIRE_TIME = 30;
    private static final Integer MAX_RETRY = 3;
    private static final Integer RETRY_INTERBAL_MILLIS = 200;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    public DistributedLock(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String acquire(String lockName) {
        String lockKey = LOCK_KEY_PREFIX + lockName;
        String lockValue = UUID.randomUUID().toString();
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
            if (success) {
                return lockValue;
            }
            try {
                Thread.sleep(RETRY_INTERBAL_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retryCount++;
        }
        return null;
    }

    public boolean release(String lockName, String lockValue) {
        String lockKey = LOCK_KEY_PREFIX + lockName;
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);

        if (Objects.equals(currentValue, lockValue)) {
            redisTemplate.delete(lockKey);
            return true;
        }else{
            return false;
        }
    }
}
