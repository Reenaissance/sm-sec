package com.seckill.smseckill.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.vo.LoginVo;
import com.seckill.smseckill.vo.RegisterVo;
import com.seckill.smseckill.vo.RespBean;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author vanessa
 * @since 2023-01-12
 */
public interface UserService extends IService<User> {
    IPage<User> getUserList(Page<User> page);
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    RespBean doRegister(RegisterVo registerVo, HttpServletRequest request, HttpServletResponse response);
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
    User findByMobile(Long mobie);
}
