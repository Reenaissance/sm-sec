package com.seckill.smseckill.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.smseckill.entity.Goods;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vanessa
 * @since 2023-02-19
 */
public interface GoodsService extends IService<Goods>{
    List<Goods> findAll();
    Goods findOne(Long id);
    List<Goods> search(String keyWord);
    boolean deleteGoods(Long id);
    Boolean addGoods(Goods goods);
    boolean updateGoods(Goods goods);
    Long getMaxId();
    Boolean decreaseStock(Long goodsId);
    Integer getStockById(Long goodsId);

    void setGoodsStock(Long goodsId);
}
