package com.seckill.smseckill.controller;

import com.alibaba.fastjson.JSON;
import com.seckill.smseckill.entity.Goods;
import com.seckill.smseckill.entity.Orders;
import com.seckill.smseckill.entity.SeckillRequest;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.exception.GlobalException;
import com.seckill.smseckill.rabbitmq.MQSender;
import com.seckill.smseckill.service.GoodsService;
import com.seckill.smseckill.service.OrdersService;
import com.seckill.smseckill.service.SeckillService;
import com.seckill.smseckill.utils.*;

import com.seckill.smseckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: SeckillController
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/20 22:01
 * @Version: 1.0
 */
@Slf4j
@Controller
@RequestMapping("/smseckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private GoodsService goodsService;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    RateLimiterUtil rateLimiterUtil;
    @Autowired
    private DistributedLock distributedLock;
    private ConcurrentHashMap<Long, Boolean> stockMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Goods> list = goodsService.findAll();
        String cacheKey = "sm-seckill:goods:goods_";
        String key = "sm-seckill:stock:stock_";
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goods -> {
            redisTemplate.opsForValue().set(cacheKey + goods.getId(), goods,120,TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(key + goods.getId(), goods.getStock());
            stockMap.put(goods.getId(), false);
        });
    }


    public Goods getGoodsById(Long goodsId) {
        Goods goods = null;
        String cacheKey = "sm-seckill:goods:goods_" + goodsId;
        String goodsJson = redisTemplate.opsForValue().get(cacheKey).toString();
        if (goodsJson != null) {
            goods = JSON.parseObject(goodsJson, Goods.class);
        } else {
            goods = goodsService.findOne(goodsId);
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(goods), 60, TimeUnit.SECONDS);
        }
        return goods;
    }

    public boolean decreaseStock(Long goodsId) {
        String key = "stock_" + goodsId;
        Integer stock = (Integer) redisTemplate.opsForValue().get(key);
        if (stock == null) {
            stock = goodsService.getStockById(goodsId);
            redisTemplate.opsForValue().set(key, stock, 30, TimeUnit.SECONDS);
        }
        if (stock <= 0) {
            return false;
        }
        //使用Redis的原子性操作来避免并发问题，使用Lua脚本保证减库存操作的原子性
        String script = "if redis.call('exists', KEYS[1]) == 1 then " +
                "if tonumber(redis.call('get', KEYS[1])) <= 0 then return -2; end; " +
                "redis.call('decr', KEYS[1]);return 1;end;" +
                "return -1;";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>(script, Long.class);
        Long result = (Long) redisTemplate.execute(redisScript, Collections.singletonList(key));
        if (result <= 0) {
            if (result == -2) {
                stockMap.put(goodsId, true);
                redisTemplate.opsForValue().set("isStockEmpty:" + goodsId, "0",60,TimeUnit.SECONDS);
            }
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping("/{path}/seckill")
    //@RequestMapping("/test/seckill")
    public ResponseEntity<String> seckill(User user, @PathVariable String path, Long goodsId) {
    //public ResponseEntity<String> seckill(User user, Long goodsId) {

        // 服务端分布式锁限流
        //String lockName = String.valueOf(goodsId);
        //String lockValue = distributedLock.acquire(lockName);
        //if (lockValue == null) {
        //    return ResponseEntity.status(HttpStatus.CONFLICT).body("no lock ");
        //}
        //System.out.println("------枷锁");
        //try {
            // 客户端限流，从令牌桶中获取令牌
            boolean acquire = rateLimiterUtil.acquire();
            if (!acquire) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Request too frequently");
            }

            if (user == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("please login!");
            }

            String seckillPathKey = "sm-seckill:seckillPath:" + user.getId() + "-" + goodsId;
            String real = (String) redisTemplate.opsForValue().get(seckillPathKey);
            if (StringUtils.isEmpty(path) || !path.equals(real)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("error:illegal request");
            }
            if (stockMap.get(goodsId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("error:stock not enough");
            }
            Goods goods = this.getGoodsById(goodsId);
            if (goods == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error:the goods dose not exists");
            }
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime seckillStartTime = LocalDateTime.parse(goods.getSeckillStartTime(), df);
            LocalDateTime seckillEndTime = LocalDateTime.parse(goods.getSeckillEndTime(), df);
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(seckillStartTime)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error:seckill has not started yet");
            }
            if (now.isAfter(seckillEndTime)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error:seckill has ended");
            }

            String seckillKey = "sm-seckill:orders:" + user.getId() + ":good-" + goodsId;
            Boolean hasKey = redisTemplate.hasKey(seckillKey);
            if (hasKey != null && hasKey) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("error:has seckilled");
            }

            Boolean success = this.decreaseStock(goodsId);
            if (!success) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("error:stock not enough");
            }

            SeckillRequest seckillRequest = new SeckillRequest(user, goodsId);
            mqSender.send(seckillRequest);
            return ResponseEntity.ok("0");
        //}catch (Exception e){
        //    System.out.println("-------异常解锁");
        //    distributedLock.release(lockName, lockValue);
        //    throw e;
        //}

    }

    @GetMapping("/getOrderId")
    public ResponseEntity getOrder(User user, Long goodsId) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("请登录");
        }
        String seckillKey = "sm-seckill:orders:" + user.getId() + ":good-" + goodsId;
        Long orderId = (Long) redisTemplate.opsForValue().get(seckillKey);
        if (orderId != null) {
            return ResponseEntity.ok(orderId);
        } else if (Boolean.TRUE.equals(redisTemplate.hasKey("isStockEmpty:" + goodsId))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("该商品没有库存");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("订单生成失败");
        }
    }

    @RequestMapping("/getPath")
    public ResponseEntity getPath(User user, Long goodsId, String captcha) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("未登录");
        }
        //captcha = "0";
        if (StringUtils.isEmpty(captcha)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("请输入验证码");
        }
        if (!StringUtils.isEmpty(captcha)) {
            String captchaKey = "sm-seckill:captcha:" + user.getId() + "-" + goodsId;
            String answer = String.valueOf(redisTemplate.opsForValue().get(captchaKey)) ;
            if (!captcha.equals(answer)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("验证码错误");
            }
        }
        String s = MD5Util.md5(UUIDUtil.uuid() + "helloFuckingWorld");
        String seckillPathKey = "sm-seckill:seckillPath:" + user.getId() + "-" + goodsId;
        redisTemplate.opsForValue().set(seckillPathKey, s, 60, TimeUnit.SECONDS);
        return ResponseEntity.ok(s);
    }

    @RequestMapping(value = "/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.ILLEGAL_REQUEST);
        }
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        ArithmeticCaptcha specCaptcha = new ArithmeticCaptcha(95, 32, 3);
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        String captchaKey = "sm-seckill:captcha:" + user.getId() + "-" + goodsId;
        redisTemplate.opsForValue().set(captchaKey, specCaptcha.text(), 300, TimeUnit.SECONDS);
        try {
            specCaptcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }

    }
}
