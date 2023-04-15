package com.seckill.smseckill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.rabbitmq.MQSender;
import com.seckill.smseckill.service.UserService;
import com.seckill.smseckill.utils.CookieUtil;
import com.seckill.smseckill.utils.MD5Util;
import com.seckill.smseckill.vo.RespBean;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author vanessa
 * @since 2023-01-12
 */
@Controller
@RequestMapping("/smseckill")
public class UserController {
    @Autowired
    private MQSender mqSender;
    @Autowired
    private UserService userService;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @RequestMapping("/index")
    public String toIndex(User user){
        if (user == null) {
            return "login";
        }
        return "index";
    }
    @RequestMapping("/test")
    public String toTest(){
        return "login";
    }
    @RequestMapping("/GoodsDetail")
    public String goodsDetail(@RequestParam(value = "goodsId", required = false) Long goodsId, HttpServletRequest request, HttpServletResponse response){
        if (goodsId != null){
            CookieUtil.setCookie(request, response, "FavGoodsId", String.valueOf(goodsId));
        }
        return "index";
    }
    @RequestMapping("/admin")
    public String toAdmin(User user){
        if (user != null && Objects.equals(user.getRole(), "admin")){
            return "admin";
        } else {
            return "index";
        }
    }
    @RequestMapping("/user")
    public String toUser(User user){
        if (user != null && Objects.equals(user.getRole(), "admin")){
            return "user";
        } else {
            return "index";
        }
    }
    @RequestMapping("/auditLog")
    public String toLog(User user){
        if (user != null && Objects.equals(user.getRole(), "admin")){
            return "log";
        } else {
            return "index";
        }
    }
    @RequestMapping("/profile")
    public String toProfile(User user){
        //if (user == null){
        //    return "login";
        //}
        return "profile";
    }
    @RequestMapping("/orderDetail")
    public String toOrderDetail(){
        return "orderDetail";
    }
    @RequestMapping("/orders")
    public String toOrder(User user){
        if (user != null && Objects.equals(user.getRole(), "admin")){
            return "orders";
        }
        return "index";
    }
    @RequestMapping("/userInfo")
    @ResponseBody
    public RespBean userInfo(User user){
        return RespBean.success(user);
    }
    @PutMapping("/updateUser")
    @ResponseBody
    public User updateUser(Long id, String nickname, String password, HttpServletRequest request) throws Exception {
        //Long mobile = inputUser.getId();
        User user = userService.findByMobile(id);
        if (user == null) {
            throw new NotFoundException("该用户不存在");
        }
        if (StringUtils.isEmpty(nickname)) {
            nickname = user.getNickname();
        }
        String inputPassword = MD5Util.fromPassToDBPass(password, user.getSalt());
        if (StringUtils.isEmpty(password)) {
            inputPassword = user.getPassword();
        }
        user.setNickname(nickname);
        user.setPassword(inputPassword);
        userService.updateById(user);
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        redisTemplate.opsForValue().set("sm-seckill:session:user:" + userTicket, user);
        return user;
    }
    @GetMapping("/findAll")
    @ResponseBody
    public Map<String, Object> getUserList(@RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> result = userService.getUserList(page);
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        data.put("pagenum", pageNum);
        data.put("pagesize", pageSize);
        data.put("list", result.getRecords());
        return data;
    }
    @GetMapping("/findUser")
    @ResponseBody
    public User findUser(Long id){
        return userService.findByMobile(id);
    }
}
