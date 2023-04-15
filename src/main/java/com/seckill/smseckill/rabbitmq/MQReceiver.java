package com.seckill.smseckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.seckill.smseckill.config.RabbitMQConfig;
import com.seckill.smseckill.entity.Goods;
import com.seckill.smseckill.entity.Orders;
import com.seckill.smseckill.entity.SeckillRequest;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.service.GoodsService;
import com.seckill.smseckill.service.OrdersService;
import com.seckill.smseckill.utils.DistributedLock;
import com.seckill.smseckill.utils.UUIDUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.amqp.core.Message;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.rabbitmq
 * @ClassName: MQReceiver
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/8 22:07
 * @Version: 1.0
 */
@Slf4j
@Service
public class MQReceiver {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private GoodsService goodsService;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private DistributedLock distributedLock;
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(Message message, SeckillRequest request, Channel channel) throws IOException {
        Long goodsId = request.getGoodsId();
        User user = request.getUser();
        log.info("收到订单消息，用户为：{}，商品为：{}", user.getId(), goodsId);
        Goods goods = goodsService.findOne(goodsId);
        if (goods.getStock() < 1){
            return;
        }
        String seckillKey = "sm-seckill:orders:" + user.getId() + ":good-" + goodsId;
        if (redisTemplate.opsForValue().get(seckillKey) != null){
            return;
        }
        Orders order = new Orders();
        order.setOrderId(UUIDUtil.uuidToLong());
        order.setUserId(user.getId());
        order.setGoodsId(goodsId);
        order.setStatus((byte) 1);
        order.setCreateDate(LocalDateTime.now());
        Boolean isSave = ordersService.createOrder(order);
        //Boolean isDecr = goodsService.decreaseStock(goodsId);
        if (isSave){
            //mqSender.send(order.getOrderId().toString());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            //System.out.println(System.currentTimeMillis());
            log.info("消费订单成功，用户为：{}，商品为：{}", user.getId(), goodsId);
        }else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            log.info("消费订单失败，用户为：{}，商品为：{}", user.getId(), goodsId);
        }
        //String lockName = String.valueOf(goodsId);
        //distributedLock.release(lockName, request.getLockValue());
        //System.out.println("------解锁");

    }
    //@RabbitListener(queues = RabbitMQConfig.ORDER_SUCCESS_QUEUE)
    //public void handleSeckillOrder(String message) {
    //    log.info("receive order:{}",message);
    //
    //}
}
