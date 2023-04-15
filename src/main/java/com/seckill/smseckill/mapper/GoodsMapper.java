package com.seckill.smseckill.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.smseckill.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
public interface GoodsMapper extends BaseMapper<Goods> {
    @Select("SELECT MAX(id) FROM `t_goods`")
    Long getMaxId();

    @Update("UPDATE `t_goods` SET stock = stock - 1 WHERE id = #{id}  AND stock > 0")
    int decreaseStock(@Param("id")Long id);
}
