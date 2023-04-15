package com.seckill.smseckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.smseckill.entity.Auditlog;
import com.seckill.smseckill.entity.Goods;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.mapper.AuditLogMapper;
import com.seckill.smseckill.service.GoodsService;
import com.seckill.smseckill.vo.RespBean;
import com.seckill.smseckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: GoodsController
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/12 17:49
 * @Version: 1.0
 */
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @GetMapping("/adminList")
    @ResponseBody
    public List<Goods> adminListGoods() {
        List<Goods> goodsList = goodsService.findAll();
        return goodsList;
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Goods> listGoods() {
        String cacheKey = "sm-seckill:goods:list";
        String cacheValue = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null) {
            List<Goods> goodsList = JSON.parseArray(cacheValue, Goods.class);
            return goodsList;
        } else {
            List<Goods> goodsList = goodsService.findAll();
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(goodsList), 30, TimeUnit.SECONDS);
            return goodsList;
        }
    }

    @RequestMapping("/test")
    @ResponseBody
    public Goods findGood(Long goodsId) {
        return goodsService.findOne(goodsId);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Goods oneGoods(@PathVariable("id") Long id) {
        return goodsService.findOne(id);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Goods> searchGoods(@RequestParam("keyword") String keyWord) {
        return goodsService.search(keyWord);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGoods(User user, @PathVariable("id") Long id) {
        boolean result = goodsService.deleteGoods(id);
        Auditlog auditlog = new Auditlog();
        auditlog.setUserId(user.getId());
        auditlog.setOperation("DELETE GOODS-" + id + ":" + (result ? "SUCCESS" : "FAILED"));
        auditlog.setTimestamp(LocalDateTime.now());
        auditLogMapper.insert(auditlog);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/max-id")
    @ResponseBody
    public Long getMaxId() {
        return goodsService.getMaxId();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateGoods(User user,
                                            @RequestParam("isUpdate") Boolean isUpdate,
                                            @RequestParam("id") Long id,
                                            @RequestParam("name") String name,
                                            @RequestParam("title") String title,
                                            @RequestParam("price") BigDecimal price,
                                            @RequestParam("stock") Integer stock,
                                            @RequestParam("detail") String detail,
                                            @RequestParam(value = "img", required = false) MultipartFile img,
                                            @RequestParam("seckillStartTime") String seckillStartTime,
                                            @RequestParam("seckillEndTime") String seckillEndTime,
                                            @RequestParam("status") Byte status) throws IOException {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.valueOf("请先登录")).build();
        }
        Goods destGoods = new Goods();
        if (isUpdate) {
            destGoods = goodsService.findOne(id);
            if (destGoods == null) {
                return ResponseEntity.status(HttpStatus.valueOf("该商品不存在")).build();
            }
        } else {
            destGoods.setId(id);
            destGoods.setAddtime(LocalDateTime.now());
        }
        destGoods.setName(name);
        destGoods.setTitle(title);
        destGoods.setPrice(price);
        destGoods.setStock(stock);
        destGoods.setDetail(detail);
        destGoods.setSeckillStartTime(seckillStartTime);
        destGoods.setSeckillEndTime(seckillEndTime);
        destGoods.setStatus(status);
        if (img != null && !img.isEmpty()) {
            String imgUrl = saveImage(img);
            destGoods.setImg(imgUrl);
        }
        boolean success = false;
        if (isUpdate) {
            success = goodsService.updateGoods(destGoods);
        } else {
            success = goodsService.addGoods(destGoods);
        }
        Auditlog auditlog = new Auditlog();
        auditlog.setUserId(user.getId());
        auditlog.setOperation((isUpdate ? "UPDATE " : "INSERT ") + "GOODS-" + id + ":" + (success ? " SUCCESS" : " FAILED"));
        auditlog.setTimestamp(LocalDateTime.now());
        auditLogMapper.insert(auditlog);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        String path = resourceLoader.getResource("classpath:static/").getFile().getAbsolutePath();
        File imgDir = new File(path + "/img");
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        File saveFile = new File(imgDir, fileName);
        try {
            image.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return "img/" + fileName;
    }
}

