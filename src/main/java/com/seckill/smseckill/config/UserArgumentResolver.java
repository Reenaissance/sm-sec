package com.seckill.smseckill.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.service.UserService;
import com.seckill.smseckill.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.config
 * @ClassName: UserArgumentResolver
 * @Author: Vanessa
 * @Description:
 *              springmvc所有的映射方法中形式参数的值注入都是靠springmvc自带参数解析器实现
 *              通过WebMvcConfigure结合HandlerMethodArgumentResolver，
 *              在每个接口带有特定参数的时候进行获取用户的唯一标识。
 *              可以减少代码重复问题
 * @Date: 2023/2/15 1:05
 * @Version: 1.0
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private UserService userService;
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeRequest(HttpServletResponse.class);
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        return userService.getUserByCookie(userTicket, request, response);
    }
}
