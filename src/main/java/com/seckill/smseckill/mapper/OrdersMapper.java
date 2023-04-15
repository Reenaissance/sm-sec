package com.seckill.smseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.smseckill.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import com.seckill.smseckill.entity.Orders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author vanessa
 * @since 2023-02-19
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    @Select("SELECT * FROM `t_orders` WHERE user_id = #{userId} \n" +
            "AND goods_id = #{goodsId}")
    Orders selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    @Select("SELECT o.*, g.`name`, g.`img`, g.`price`\n" +
            "FROM `t_orders` o\n" +
            "INNER JOIN `t_goods` g \n" +
            "ON o.goods_id = g.id WHERE o.`order_id` = #{orderId}")
    OrderDetail showOrderDetail(@Param("orderId")String orderId);
    @Select("SELECT o.*, g.`name`, g.`img`, g.`price`\n" +
            "FROM `t_orders` o\n" +
            "INNER JOIN `t_goods` g \n" +
            "ON o.goods_id = g.id WHERE o.`user_id` = #{userId}")
    List<OrderDetail> getOrderByUserId(Long userId);
    @Select("SELECT o.*, g.`name`, g.`img`, g.`price`\n" +
            "FROM `t_orders` o\n" +
            "INNER JOIN `t_goods` g \n" +
            "ON o.goods_id = g.id")
    List<OrderDetail> selectAllDetail();
}
