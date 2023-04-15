package com.seckill.smseckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.vo
 * @ClassName: RespBeanEnum
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/5 18:19
 * @Version: 1.0
 */
@ToString
@AllArgsConstructor
@Getter
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500, "服务端异常"),
    //login
    BIND_ERROR(500210, "参数校验异常"),
    LOGIN_ERROR(500214, "用户名或密码不正确"),
    ILLEGAL_REQUEST(500215,"非法请求");
    private final Integer code;
    private final String message;

}
