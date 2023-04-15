package com.seckill.smseckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.entity
 * @ClassName: OrderDetail
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/28 1:53
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private String orderId;

    private Long userId;

    private Long goodsId;

    private Byte status;

    private LocalDateTime createDate;

    private String name;

    private String img;

    private BigDecimal price;
}
