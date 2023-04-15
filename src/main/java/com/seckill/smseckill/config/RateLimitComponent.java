package com.seckill.smseckill.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.BoundHashOperations;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: RateLimiterUtil
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/12 2:19
 * @Version: 1.0
 */
@Component
public class RateLimitComponent  {
    private static final String BUCKET_SIZE_KEY = "rateLimit:bucketSize";
    private static final String RATE_KEY = "rateLimit:rate";
    private static final String TOKEN_COUNT_KEY = "rateLimit:tokenCount";
    private static final String LAST_TOKEN_TIME_KEY = "rateLimit:lastTokenTime";
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        // 桶的容量
        int bucketSize = 2;
        // rate通常每秒
        int rate = 1;
        //int tokenCount = bucketSize / 2;
        int tokenCount = bucketSize;
        long now = System.currentTimeMillis();
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps("rateLimit");
        ops.put(BUCKET_SIZE_KEY, String.valueOf(bucketSize));
        ops.put(RATE_KEY, String.valueOf(rate));
        redisTemplate.opsForValue().set(TOKEN_COUNT_KEY, tokenCount);
        redisTemplate.opsForValue().set(LAST_TOKEN_TIME_KEY, String.valueOf(now));
    }

    public int getBucketSize() {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps("rateLimit");
        return Integer.parseInt(String.valueOf(ops.get(BUCKET_SIZE_KEY)));
    }

    public int getRate() {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps("rateLimit");
        return Integer.parseInt(String.valueOf(ops.get(RATE_KEY)));
    }
}

