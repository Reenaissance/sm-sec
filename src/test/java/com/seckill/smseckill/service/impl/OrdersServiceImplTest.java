package com.seckill.smseckill.service.impl;

import com.seckill.smseckill.entity.OrderDetail;
import com.seckill.smseckill.service.OrdersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.service.impl
 * @ClassName: OrdersServiceImplTest
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/14 23:46
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrdersServiceImplTest {
    @Autowired
    private OrdersService ordersService;
    @Test
    public void test(){
        //OrderDetail orderDetail = ordersService.showOrder(2056700696134848800L);
        //System.out.println(orderDetail.toString());
        //if(ordersService == null){
        //    System.out.println("error");
        //    return;
        //}
        List<OrderDetail> orderByUserId = ordersService.getOrderByUserId(15819571572L);
        System.out.println(Arrays.toString(orderByUserId.toArray()));
    }

}