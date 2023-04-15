package com.seckill.smseckill.controller;

import com.seckill.smseckill.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: SeckillControllerTest
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/7 2:34
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SeckillControllerTest {
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;

    @Test
    public void test(){
        Long goodsId = 10001L;
        String key = "stock:stock_" + goodsId;
        Integer stock = (Integer)redisTemplate.opsForValue().get(key);
        if (stock == null) {
            stock = goodsService.getStockById(goodsId);
            redisTemplate.opsForValue().set(key, stock, 60, TimeUnit.SECONDS);
        }
        if (stock <= 0) {
            System.out.println("stock <= 0");
        }
        //使用Redis的原子性操作来避免并发问题，使用Lua脚本保证减库存操作的原子性
        String script = "if redis.call('exists', KEYS[1]) == 1 then " +
                "if tonumber(redis.call('get', KEYS[1])) <= 0 then return -2; end; " +
                "redis.call('decr', KEYS[1]);return 1;end;" +
                "return -1;";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>(script, Long.class);
        Long result = (Long) redisTemplate.execute(redisScript, Collections.singletonList(key));
        if (result <= 0) {
            if(result == -2){
                goodsService.setGoodsStock(goodsId);
            }
            System.out.println("result <= 0");
        }else {
            System.out.println(true);
        }
    }

}