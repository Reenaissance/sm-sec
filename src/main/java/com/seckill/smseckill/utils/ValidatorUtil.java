package com.seckill.smseckill.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: ValidatorUtil
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/9 23:52
 * @Version: 1.0
 */
public class ValidatorUtil {

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if ((phoneNumber != null) && (!phoneNumber.isEmpty())) {
            return Pattern.matches("^1[3-9]\\d{9}$", phoneNumber);
        }
        return false;
    }

}

