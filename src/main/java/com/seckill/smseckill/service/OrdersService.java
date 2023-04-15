package com.seckill.smseckill.service;

import com.seckill.smseckill.entity.OrderDetail;
import com.seckill.smseckill.entity.Orders;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vanessa
 * @since 2023-02-19
 */
public interface OrdersService{
    Boolean createOrder(Orders orders);
    Orders findOrder(Long userId, Long goodsId);
    OrderDetail showOrder(String orderId);
    List<OrderDetail> getOrderByUserId(Long userId);
    List<OrderDetail> getAllOrder();
}
