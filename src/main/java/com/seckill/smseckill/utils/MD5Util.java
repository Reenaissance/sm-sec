package com.seckill.smseckill.utils;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: MD5Util
 * @Author: Vanessa
 * @Description: 主要对用户密码加密，逻辑加密有两次，
 *              一次为前端传送过来对密码的一次加密，第二次为后端对密码再加密后存入数据库
 *              MD5没有可能解密，只是哈希，不是加密，安全性高
 * @Date: 2023/1/6 21:30
 * @Version: 1.0
 */
@Component
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }
    // 前后端共享salt
    private static final String salt = "1a2b3c4d";
    public static String inputPassToFromPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }
    /**
     * @Method fromPassToDBPass
     * @Author Vanessa
     * @Param salt 过度密码 salt为每个用户专门随机生成的salt，需存储进数据库字段中
     * @Date 2023/1/7 17:45
     **/
    public static String fromPassToDBPass(String formPass,String salt){
        String str = "" + salt.charAt(1)+salt.charAt(3)+formPass+salt.charAt(5)+salt.charAt(2);
        return md5(str);
    }
    public  static String inputPassToDBPass(String inputPass,String salt){
        String formPass = inputPassToFromPass(inputPass);
        return fromPassToDBPass(formPass,salt);
    }
    public static String generateSalt() {
        // 生成一个随机的盐值
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[10];
        random.nextBytes(saltBytes);
        String salt = Hex.encodeHexString(saltBytes);
        if (salt.length() > 10){
            salt = salt.substring(0,8);
        }
        return salt;
    }
    public static void main(String[] args) {
        String input = "123456";
        System.out.println(inputPassToFromPass("123456"));
        String fromPass = "d3b1294a61a07da9b49b6e22b2cbd7f9";
        System.out.println(fromPassToDBPass(fromPass, "695482ce"));
        System.out.println(inputPassToDBPass(input,"695482ce"));
    }
}
