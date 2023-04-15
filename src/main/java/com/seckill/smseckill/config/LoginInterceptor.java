package com.seckill.smseckill.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.exception.GlobalException;
import com.seckill.smseckill.service.UserService;
import com.seckill.smseckill.utils.CookieUtil;
import com.seckill.smseckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.config
 * @ClassName: LoginInterceptor
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/15 23:18
 * @Version: 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //System.out.println(request.getRequestURL());
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isBlank(userTicket)){
            response.sendRedirect(request.getContextPath() + "/smseckill/toLogin");
            return false;
        }
        User curUser = userService.getUserByCookie(userTicket, request, response);
        if(null == curUser){
            response.sendRedirect(request.getContextPath() + "/smseckill/toLogin");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
