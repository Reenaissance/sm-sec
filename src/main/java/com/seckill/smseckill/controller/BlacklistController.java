package com.seckill.smseckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.smseckill.entity.Auditlog;
import com.seckill.smseckill.entity.Blacklist;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.mapper.AuditLogMapper;
import com.seckill.smseckill.mapper.BlacklistMapper;
import com.seckill.smseckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: BlacklistController
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 1:32
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class BlacklistController {
    @Autowired
    private BlacklistMapper blacklistMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private AuditLogMapper auditLogMapper;
    @GetMapping("/findBlack")
    @ResponseBody
    public Blacklist findBlack(Long userId){
        QueryWrapper<Blacklist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Blacklist blacklist = blacklistMapper.selectOne(queryWrapper);
        return blacklist;
    }
    @GetMapping("/findAllBlack")
    @ResponseBody
    public List<Blacklist> findAllBlack(){
        QueryWrapper<Blacklist> queryWrapper = new QueryWrapper<>();
        return blacklistMapper.selectList(queryWrapper);
    }
    @DeleteMapping("/notBlack/{userId}")
    public ResponseEntity<Void> deleteBlack(User operator, @PathVariable("userId") Long userId) {
        User user = userService.findByMobile(userId);
        if (user != null) {
            QueryWrapper<Blacklist> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            int rows = blacklistMapper.delete(queryWrapper);
            boolean result = rows > 0;
            Auditlog auditlog = new Auditlog();
            auditlog.setUserId(operator.getId());
            auditlog.setOperation("ACTIVE BLACKED USER-" + userId + ": " + (result ? "SUCCESS" : "FAILED"));
            auditlog.setTimestamp(LocalDateTime.now());
            auditLogMapper.insert(auditlog);
            if (result) {
                user.setRole("consumer");
                UpdateWrapper<User> wrapper = new UpdateWrapper<>();
                wrapper.eq("id", user.getId());
                userService.update(user, wrapper);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/black")
    public ResponseEntity<String> insertBlack(User operator, @RequestParam("userId") Long userId, @RequestParam("reason") String reason) {
        User user = userService.findByMobile(userId);
        Auditlog auditlog = new Auditlog();
        auditlog.setUserId(operator.getId());
        auditlog.setTimestamp(LocalDateTime.now());
        if (user != null && !Objects.equals(user.getRole(), "blacked")) {
            Blacklist blacklist = new Blacklist();
            blacklist.setUserId(userId);
            blacklist.setUserName(user.getNickname());
            blacklist.setReason(reason);
            blacklist.setCreateTime(LocalDateTime.now());
            blacklistMapper.insert(blacklist);

            user.setRole("blacked");
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", user.getId());
            boolean update = userService.update(user, wrapper);
            auditlog.setOperation("BLACK USER-" + userId + ": " + (update ? "SUCCESS" : "FAILED"));
            auditLogMapper.insert(auditlog);
            if (update) {
                return ResponseEntity.status(HttpStatus.OK).body("拉黑成功！");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("异常拉黑！");
            }
        } else {
            auditlog.setOperation("BLACK USER-" + userId + ": " + "FAILED");
            auditLogMapper.insert(auditlog);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("用户已在黑名单中，请不要重复操作");
        }
    }
    @GetMapping("/blacklist")
    public Map<String, Object> getBlacklist(@RequestParam(value = "page", defaultValue = "1") Integer page) {
        Page<Blacklist> pager = new Page<>(page, 10);// 每页10条记录
        List<Blacklist> blacklistList = blacklistMapper.selectBlacklistPage(pager);
        Map<String, Object> data = new HashMap<>();
        data.put("blacklistPage", pager);
        data.put("blacklistList", blacklistList);
        return data;
    }
}