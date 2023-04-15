package com.seckill.smseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.smseckill.entity.Blacklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.mapper
 * @ClassName: BlacklistMapper
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 1:31
 * @Version: 1.0
 */
@Mapper
public interface BlacklistMapper extends BaseMapper<Blacklist> {
    @Select("SELECT * FROM `t_blacklist`")
    List<Blacklist> selectBlacklistPage(Page<Blacklist> page);
}