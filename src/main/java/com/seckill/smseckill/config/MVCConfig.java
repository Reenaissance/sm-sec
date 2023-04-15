package com.seckill.smseckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.config
 * @ClassName: MVCConfig
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/14 23:23
 * @Version: 1.0
 */

@Configuration
public class MVCConfig implements WebMvcConfigurer {
    @Autowired
    private UserArgumentResolver userArgumentResolver;

    //@Override
    //public void addInterceptors(InterceptorRegistry registry) {
    //    registry.addInterceptor(new LoginInterceptor())
    //            .excludePathPatterns("/smseckill/toLogin")
    //            .addPathPatterns("/**");
    //}

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}
