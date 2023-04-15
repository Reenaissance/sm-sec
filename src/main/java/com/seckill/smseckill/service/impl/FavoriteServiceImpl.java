package com.seckill.smseckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.smseckill.entity.Favorite;
import com.seckill.smseckill.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.service.impl
 * @ClassName: FavoriteServiceImpl
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 20:37
 * @Version: 1.0
 */
@Service
public class FavoriteServiceImpl {
    @Autowired
    private FavoriteMapper favoriteMapper;
    public List<Favorite> getFavoritesByUserId(Long userId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Favorite::getUserId, userId);
        return favoriteMapper.selectList(queryWrapper);
    }
    public void addFav(Long goodsId, Long userId) {
        Favorite favorite = new Favorite();
        favorite.setGoodsId(goodsId);
        favorite.setUserId(userId);
        favoriteMapper.insert(favorite);
    }

    public void removeFav(Long goodsId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("goods_id", goodsId);
        params.put("user_id", userId);
        favoriteMapper.deleteByMap(params);
    }

    public boolean checkFav(Long goodsId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("goods_id", goodsId);
        params.put("user_id", userId);
        boolean result = favoriteMapper.selectByMap(params).size() > 0;
        //System.out.println(goodsId + ":" + result);
        return result;
    }
}
