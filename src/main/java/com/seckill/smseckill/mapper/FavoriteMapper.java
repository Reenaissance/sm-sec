package com.seckill.smseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.smseckill.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.mapper
 * @ClassName: FavoriteMapper
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 20:38
 * @Version: 1.0
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {}
