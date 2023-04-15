package com.seckill.smseckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author vanessa
 * @since 2023-02-27
 */
@Getter
@Setter
@TableName("t_orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("goods_id")
    private Long goodsId;

    @TableField("status")
    private Byte status;

    @TableField("create_date")
    private LocalDateTime createDate;

}
