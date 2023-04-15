package com.seckill.smseckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author vanessa
 * @since 2023-02-19
 */
@Getter
@Setter
@TableName("t_goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("title")
    private String title;

    @TableField("img")
    private String img;

    @TableField("detail")
    private String detail;

    @TableField("price")
    private BigDecimal price;

    @TableField("addtime")
    private LocalDateTime addtime;

    @TableField("seckill_start_time")
    private String seckillStartTime;

    @TableField("seckill_end_time")
    private String seckillEndTime;

    @TableField("status")
    private Byte status;

    @TableField("stock")
    private Integer stock;
}
