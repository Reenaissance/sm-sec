package com.seckill.smseckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.vo
 * @ClassName: RegisterVo
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/14 12:07
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVo {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
}
