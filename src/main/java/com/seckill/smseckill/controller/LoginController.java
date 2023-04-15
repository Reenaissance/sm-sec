package com.seckill.smseckill.controller;

import com.seckill.smseckill.service.UserService;
import com.seckill.smseckill.utils.CookieUtil;
import com.seckill.smseckill.vo.LoginVo;
import com.seckill.smseckill.vo.RegisterVo;
import com.seckill.smseckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: LoginController
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/1/12 22:30
 * @Version: 1.0
 */
@Slf4j
@Controller
@RequestMapping("/smseckill")
public class LoginController {
    @Autowired
    private UserService userService;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        return userService.doLogin(loginVo,request,response);
    }
    @PostMapping("/register")
    @ResponseBody
    public RespBean register(RegisterVo userVo, HttpServletRequest request, HttpServletResponse response) {
         return userService.doRegister(userVo,request,response);
    }
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isNotBlank(userTicket)){
            CookieUtil.deleteCookie(request,response,"userTicket");
        }
        return "redirect:/smseckill/toLogin";
    }
}
