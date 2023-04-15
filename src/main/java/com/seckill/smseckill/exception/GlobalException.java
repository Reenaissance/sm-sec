package com.seckill.smseckill.exception;

import com.seckill.smseckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.exception
 * @ClassName: GlobalException
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/9 21:09
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends  RuntimeException{
    private RespBeanEnum respBeanEnum;
}
