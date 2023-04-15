package com.seckill.smseckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.smseckill.entity.Goods;
import com.seckill.smseckill.mapper.GoodsMapper;
import com.seckill.smseckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
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
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper,Goods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public List<Goods> findAll() {
        List<Goods> goodsList = goodsMapper.selectList(null);
        return goodsList;
    }
    @Override
    public Goods findOne(Long id){
        return goodsMapper.selectById(id);
    }
    @Override
    public List<Goods> search(String keyWord){
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("id",keyWord).or().like("name",keyWord);
        return goodsMapper.selectList(queryWrapper);
    }
    @Override
    public boolean deleteGoods(Long id) {
        return goodsMapper.deleteById(id) > 0;
    }
    @Override
    public Boolean addGoods(Goods goods) {
        return goodsMapper.insert(goods) > 0;
    }
    @Override
    public boolean updateGoods(Goods goods) {
        return goodsMapper.updateById(goods) > 0;
    }
    @Override
    public Long getMaxId(){
        return goodsMapper.getMaxId();
    }
    @Override
    public Boolean decreaseStock(Long goodsId) {
        String key = "stock_" + goodsId;
        Integer stock = (Integer)redisTemplate.opsForValue().get(key);
        if (stock == null) {
            stock = this.getStockById(goodsId);
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
            if(result == -2){
                this.setGoodsStock(goodsId);
            }
            return false;
        }else {
            return true;
        }
    }
    //public Boolean decreaseStock(Long goodsId){
    //    Goods goods = goodsMapper.selectById(goodsId);
    //    if (goods == null || goods.getStock() <= 0) {
    //        return false;
    //    }
    //    return goodsMapper.decreaseStock(goodsId) > 0;
    //}

    @Override
    public Integer getStockById(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods != null){
            return goods.getStock();
        }else{
            return 0;
        }
    }

    @Override
    public void setGoodsStock(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        goods.setStock(0);
        goodsMapper.updateById(goods);
    }

}
