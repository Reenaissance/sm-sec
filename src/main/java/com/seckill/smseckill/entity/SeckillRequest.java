package com.seckill.smseckill.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.entity
 * @ClassName: SeckillRequest
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/9 1:57
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillRequest implements Serializable {
    private User user;
    private Long goodsId;
    private String lockValue;
    public SeckillRequest(User user, Long goodsId){
        this.user = user;
        this.goodsId = goodsId;
    }
}