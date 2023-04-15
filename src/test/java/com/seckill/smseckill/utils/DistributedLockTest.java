package com.seckill.smseckill.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: DistributedLockTest
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/12 21:32
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DistributedLockTest {
    @Autowired
    private DistributedLock distributedLock;

    public void testLock() {
        String lockName = String.valueOf(10001);
        String lockValue = distributedLock.acquire(lockName);
        if (lockValue == null){
           System.out.println(false);
        }else{
            System.out.println(true);
            distributedLock.release(lockName,lockValue);
        }
    }

    @Test
    public void test(){
        for(int i = 0; i < 100; i++){
            System.out.println("当前循环编号：" + i);
            testLock();

            try {
                // 休息 1 秒钟
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}