package com.seckill.smseckill.rabbitmq;

import com.seckill.smseckill.config.RabbitMQConfig;
import com.seckill.smseckill.entity.SeckillRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.rabbitmq
 * @ClassName: MQSender
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/8 21:54
 * @Version: 1.0
 */
@Slf4j
@Service
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(SeckillRequest request){
        log.info("发送订单信息，用户为：{}，商品为：{}", request.getUser().getId(), request.getGoodsId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,RabbitMQConfig.ORDER_ROUTING_KEY, request);
    }
    public void send(String message){
        log.info("发送订单号：{}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_SUCCESS_EXCHANGE,RabbitMQConfig.ORDER_SUCCESS_KEY, message);
    }
}
