package com.seckill.smseckill.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.config
 * @ClassName: mqConfig
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/8 21:47
 * @Version: 1.0
 */
@Configuration
public class RabbitMQConfig {
    public static final String STOCK_EXCHANGE = "STOCK_EXCHANGE";
    public static final String ORDER_EXCHANGE = "ORDER_EXCHANGE";
    public static final String STOCK_QUEUE = "STOCK_QUEUE";
    public static final String ORDER_QUEUE = "ORDER_QUEUE";

    public static final String ORDER_SUCCESS_QUEUE = "ORDER_SUCCESS_QUEUE";
    public static final String ORDER_SUCCESS_EXCHANGE = "ORDER_SUCCESS_EXCHANGE";
    public static final String ORDER_SUCCESS_KEY = "ORDER_SUCCESS_KEY";

    
    public static final String STOCK_ROUTING_KEY = "STOCK_ROUTING_KEY";
    public static final String ORDER_ROUTING_KEY = "ORDER_ROUTING_KEY";


    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue getOrderSuccessQueue(){
        return new Queue(ORDER_SUCCESS_QUEUE,true);
    }
    @Bean
    public Exchange orderRequestExchange() {
        return ExchangeBuilder.directExchange(ORDER_SUCCESS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindOrderRequest() {
        return BindingBuilder.bind(getOrderSuccessQueue()).to(orderRequestExchange()).with(ORDER_SUCCESS_KEY).noargs();
    }
    @Bean
    public Queue getStockQueue() {
        return new Queue(STOCK_QUEUE,true);
    }
    @Bean
    public Exchange getStockExchange() {
        return ExchangeBuilder.directExchange(STOCK_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindStock() {
        return BindingBuilder.bind(getStockQueue()).to(getStockExchange()).with(STOCK_ROUTING_KEY).noargs();
    }
    @Bean
    public Queue getOrderQueue() {
        return new Queue(ORDER_QUEUE);
    }
    @Bean
    public Exchange getOrderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }
    @Bean
    public Binding bindOrder() {
        return BindingBuilder.bind(getOrderQueue()).to(getOrderExchange()).with(ORDER_ROUTING_KEY).noargs();
    }
    
}
