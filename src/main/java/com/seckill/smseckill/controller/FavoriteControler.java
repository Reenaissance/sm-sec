package com.seckill.smseckill.controller;

import com.seckill.smseckill.entity.Favorite;
import com.seckill.smseckill.entity.Goods;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.mapper.GoodsMapper;
import com.seckill.smseckill.service.GoodsService;
import com.seckill.smseckill.service.impl.FavoriteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: FavoriteControler
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 20:36
 * @Version: 1.0
 */
@RestController
public class FavoriteControler {
    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private GoodsMapper goodsMapper;
    @GetMapping("/favorites")
    public List<Goods> getFavorites(User user) {
        Long userId = user.getId();
        List<Favorite> favoritesByUserId = favoriteService.getFavoritesByUserId(userId);
        if (favoritesByUserId.isEmpty()){
            return null;
        }
        List<Long> goodsIds = favoritesByUserId.stream().map(Favorite::getGoodsId).collect(Collectors.toList());
        List<Goods> goodsList = goodsMapper.selectBatchIds(goodsIds);
        return goodsList;
    }

    @PostMapping("/add_fav")
    public void addToFav(User user, @RequestParam Long goodsId) {
        favoriteService.addFav(goodsId, user.getId());
    }

    @PostMapping("/remove_fav")
    public void removeFromFav(User user, @RequestParam Long goodsId) {
        favoriteService.removeFav(goodsId, user.getId());
    }

    @GetMapping("/check_fav")
    public boolean checkFav(User user, @RequestParam Long goodsId) {
        return favoriteService.checkFav(goodsId, user.getId());
    }

}
