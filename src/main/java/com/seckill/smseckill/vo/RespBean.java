package com.seckill.smseckill.vo;

import lombok.*;

import java.util.HashMap;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.vo
 * @ClassName: RespBean
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/5 18:19
 * @Version: 1.0
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespBean{
    private long code;
    private String message;
    private Object obj;

    public RespBean(String message) {
        this.message = message;
    }

    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(),null);
    }
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), obj);
    }
    public static RespBean error(String message){
        return new RespBean(message);
    }
    //存在多种失败原因，故直接传递一个对象参数
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(),null);
    }
    public static RespBean error(RespBeanEnum respBeanEnum, Object obj){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), obj);
    }
}
