package com.seckill.smseckill.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: RedisUtil
 * @Author: Vanessa
 * @Description: 使用spring的RedisTemplate进行Redis数据存取操作，会发现乱码前缀
 *              故需要对RedisTemplate实例化进行序列化和反序列化组件进行指定
 * @Date: 2023/2/13 0:22
 * @Version: 1.0
 */
@Configuration
public class RedisConfig {

    @Bean("redisStringTemplate")
    public RedisTemplate<Object, Object> redisStringTemplate(RedisConnectionFactory factory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
