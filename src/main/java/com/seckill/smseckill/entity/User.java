package com.seckill.smseckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author vanessa
 * @since 2023-03-04
 */
@Data
@Getter
@Setter
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "keyId", type = IdType.AUTO)
    private Long keyId;

    /**
     * 手机号
     */
    @TableField("id")
    private Long id;

    @TableField("nickname")
    private String nickname;

    @TableField("role")
    private String role;
    /**
     * 加密MDS(MDS(pass明文+固定salt)+salt)
     */
    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;
}
