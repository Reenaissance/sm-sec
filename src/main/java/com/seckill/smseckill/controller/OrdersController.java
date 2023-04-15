package com.seckill.smseckill.controller;

import com.seckill.smseckill.entity.OrderDetail;
import com.seckill.smseckill.entity.Orders;
import com.seckill.smseckill.service.OrdersService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vanessa
 * @since 2023-02-19
 */
@Controller
public class OrdersController {
    @Autowired
    OrdersService ordersService;
    @RequestMapping("/toOrderDetail")
    public String toOrderDetail(String orderId){
        return "orderDetail";
    }
    @RequestMapping("/getOrder")
    @ResponseBody
    public OrderDetail getOrder(@Param("orderId") String orderId) {
        return ordersService.showOrder(orderId);
    }
    @RequestMapping("/getUserOrder")
    @ResponseBody
    public List<OrderDetail> getUserOrder(@Param("userId") Long userId) {
        return ordersService.getOrderByUserId(userId);
    }
    @RequestMapping("/getAllOrder")
    @ResponseBody
    public List<OrderDetail> getAllOrder() {
        return ordersService.getAllOrder();
    }
}
