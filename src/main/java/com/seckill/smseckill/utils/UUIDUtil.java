package com.seckill.smseckill.utils;

import java.util.UUID;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: UUIDUtil
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/9 23:58
 * @Version: 1.0
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static long uuidToLong(){
        UUID uuid = UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        return mostSigBits ^ leastSigBits;
    }
}
