package com.seckill.smseckill.utils;

import com.seckill.smseckill.config.RateLimitComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: RateLimiter
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/12 3:46
 * @Version: 1.0
 */
@Component
public class RateLimiterUtil {
    //redis 中的桶 key
    private static final String BUCKET_KEY = "rateLimit:bucket";
    //记录上一个令牌下发时间的 key
    private static final String LAST_TOKEN_TIME_KEY = "rateLimit:lastTokenTime";
    //记录当前桶中令牌数量的 key
    private static final String TOKEN_COUNT_KEY = "rateLimit:tokenCount";
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    private RateLimitComponent rateLimitComponent;

    public boolean acquire() {
        int bucketSize = rateLimitComponent.getBucketSize();
        int rate = rateLimitComponent.getRate();
        BoundListOperations<String, String> bucketList = redisTemplate.boundListOps(BUCKET_KEY);

        long now = System.currentTimeMillis();
        String lastTokenTime = (String) redisTemplate.opsForValue().get(LAST_TOKEN_TIME_KEY);
        long lastTokenMillis = (lastTokenTime == null ? 0L : Long.parseLong(lastTokenTime));

        int tokenCount = (int) redisTemplate.opsForValue().get(TOKEN_COUNT_KEY);

        //计算可以从当前时间到上一个令牌下发时间中新产生的令牌数，加上之前桶中令牌的数量，得到当前桶中令牌的数量。
        int tokens = (int) ((now - lastTokenMillis) * rate / (1000)) + tokenCount;
        //确保桶中的令牌数量不超过桶的容量。如果桶中令牌数量大于桶的容量，则保留桶的容量，多余的令牌会被丢弃。
        tokens = Math.min(tokens, bucketSize);

        //表示没有令牌可供获取
        if (tokens < 1) {
            return false;
        }
        //将新产生的令牌加入桶中
        for (int i = 0; i < tokens - 1; i++) {
            bucketList.leftPush(String.valueOf(now));
        }
        //确保桶中令牌的数量不超过桶的容量
        bucketList.trim(0, bucketSize - 1);

        redisTemplate.opsForValue().set(TOKEN_COUNT_KEY, tokens - 1);
        redisTemplate.opsForValue().set(LAST_TOKEN_TIME_KEY, String.valueOf(now));
        return true;
    }
}
