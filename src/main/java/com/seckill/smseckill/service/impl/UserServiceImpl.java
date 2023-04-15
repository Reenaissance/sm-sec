package com.seckill.smseckill.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.exception.GlobalException;
import com.seckill.smseckill.mapper.UserMapper;
import com.seckill.smseckill.service.UserService;
import com.seckill.smseckill.utils.CookieUtil;
import com.seckill.smseckill.utils.UUIDUtil;
import com.seckill.smseckill.utils.MD5Util;
import com.seckill.smseckill.vo.LoginVo;
import com.seckill.smseckill.vo.RegisterVo;
import com.seckill.smseckill.vo.RespBean;
import com.seckill.smseckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author vanessa
 * @since 2023-01-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Resource(name = "redisStringTemplate")
    private RedisTemplate redisTemplate;
    @Override
    public IPage<User> getUserList(Page<User> page) {
        return baseMapper.selectPage(page, null);
    }
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",mobile);
        User user = userMapper.selectOne(queryWrapper);
        if (null == user) {
            return new RespBean("当前账号不存在");
        }
        if (!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            return new RespBean("密码错误");
        }
        //redis：解决分布式session
        //凭证是随机生成字符串generateUUID()
        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("sm-seckill:session:user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        CookieUtil.setCookie(request, response, "userId", String.valueOf(user.getId()));
        return RespBean.success(user.getRole());
    }
    public RespBean doRegister(@RequestBody RegisterVo userVo, HttpServletRequest request, HttpServletResponse response) {
        User existingUser = this.findByMobile(userVo.getId());
        if(existingUser != null) {
            return new RespBean( "该手机号已被注册");
        }

        User user = new User();
        user.setId(userVo.getId());
        user.setNickname(userVo.getNickname());

        String salt = MD5Util.generateSalt();
        user.setSalt(salt);
        user.setPassword(MD5Util.fromPassToDBPass(userVo.getPassword(), salt));
        this.save(user);
        return RespBean.success("200");
    }
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = JSONObject.parseObject(String.valueOf(redisTemplate.opsForValue().get("sm-seckill:session:user:" + userTicket)), User.class);
        if (user != null) {
            //防止cookie过期
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public User findByMobile(Long mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",mobile);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

}
