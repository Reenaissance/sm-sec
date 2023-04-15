package com.seckill.smseckill.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.seckill.smseckill.controller.SeckillController;
import com.seckill.smseckill.entity.Goods;
import com.seckill.smseckill.entity.OrderDetail;
import com.seckill.smseckill.entity.Orders;
import com.seckill.smseckill.mapper.OrdersMapper;
import com.seckill.smseckill.service.GoodsService;
import com.seckill.smseckill.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vanessa
 * @since 2023-02-19
 */
@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    OrdersMapper ordersMapper;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillController seckillController;
    @Override
    @Transactional
    public Boolean createOrder(Orders orders) {
        Goods goods = seckillController.getGoodsById(orders.getGoodsId());
        goods.setStock(goods.getStock() - 1);
        boolean update = goodsService.update(
                new UpdateWrapper<Goods>()
                        .setSql("stock = " + "stock-1")
                        .eq("id", goods.getId())
                        .gt("stock", 0)
        );
        if (!update || goods.getStock() < 1) {
            return false;
        }
        String seckillKey = "sm-seckill:orders:" + orders.getUserId() + ":good-" + orders.getGoodsId();
        redisTemplate.opsForValue().set(seckillKey, orders.getOrderId(), 5, TimeUnit.MINUTES);
        return ordersMapper.insert(orders) > 0;
    }

    @Override
    public Orders findOrder(Long userId, Long goodsId) {
        return ordersMapper.selectByUserIdAndGoodsId(userId, goodsId);
    }
    @Override
    public OrderDetail showOrder(String orderId){
        return ordersMapper.showOrderDetail(orderId);
    }

    @Override
    public List<OrderDetail> getOrderByUserId(Long userId) {
        return ordersMapper.getOrderByUserId(userId);
    }

    @Override
    public List<OrderDetail> getAllOrder() {
        return ordersMapper.selectAllDetail();
    }


}
