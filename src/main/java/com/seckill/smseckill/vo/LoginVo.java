package com.seckill.smseckill.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.vo
 * @ClassName: LoginVo
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/5 22:47
 * @Version: 1.0
 */
@Data
public class LoginVo {
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式错误")
    private String mobile;
    @NotBlank(message = "密码不能为空")
    @Length(min = 32, message = "不合法")
    private String password;
}
