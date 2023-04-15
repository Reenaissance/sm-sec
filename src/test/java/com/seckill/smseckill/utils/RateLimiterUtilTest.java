package com.seckill.smseckill.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: RateLimiterUtilTest
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/12 16:30
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RateLimiterUtilTest {
    @Autowired
    RateLimiterUtil rateLimiterUtil;
    @Test
    public void test(){
        System.out.println(rateLimiterUtil.acquire());
    }
}