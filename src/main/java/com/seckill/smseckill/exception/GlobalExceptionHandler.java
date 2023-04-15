package com.seckill.smseckill.exception;

import com.seckill.smseckill.vo.RespBean;
import com.seckill.smseckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.exception
 * @ClassName: GlobalExceptionHandler
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/12 16:20
 * @Version: 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException globalException = (GlobalException) e;
            return RespBean.error(globalException.getRespBeanEnum());
        }else if(e instanceof BindException){
            BindException bindException = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常: "+bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
