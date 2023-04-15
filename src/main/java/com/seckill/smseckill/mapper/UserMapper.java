package com.seckill.smseckill.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.vo.LoginVo;
import com.seckill.smseckill.vo.RespBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author vanessa
 * @since 2023-01-12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM `t_user` WHERE 1=1 ${ew.customSqlSegment}")
    IPage<User> findAll(@Param("page") Page<User> page, @Param(Constants.WRAPPER) Wrapper<User> wrapper);
}
