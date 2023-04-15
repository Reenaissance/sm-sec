package com.seckill.smseckill.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: CookieUtil
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/2/10 0:23
 * @Version: 1.0
 */
public final class CookieUtil {
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(cookieList[i].getValue(), "UTF-8");
                    } else {
                        retValue = cookieList[i].getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return retValue;
    }
    //public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
    //    Cookie[] cookieList = request.getCookies();
    //    if (cookieList == null || cookieName == null) {
    //        return null;
    //    }
    //    String retValue = null;
    //    try {
    //        for (int i = 0; i < cookieList.length; i++) {
    //            if (cookieList[i].getName().equals(cookieName)) {
    //                retValue = URLDecoder.decode(cookieList[i].getValue(), encodeString);
    //                break;
    //            }
    //        }
    //    } catch (UnsupportedEncodingException e) {
    //        e.printStackTrace();
    //    }
    //    return retValue;
    //}

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxage) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxage, false);
    }
    //public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
    //                             String cookieValue, boolean isEncode) {
    //    setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    //}
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxage, boolean isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, isEncode);
    }
    //public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
    //                             String cookieValue, int cookieMaxage, String encodeString) {
    //    doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, encodeString);
    //}
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName) {
        doSetCookie(request, response, cookieName, "", -1, false);
    }
    private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                          String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0)
                cookie.setMaxAge(cookieMaxage);
            if (null != request) {// 设置域名的cookie
                String domainName = getDomainName(request);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            if(response != null){
                response.addCookie(cookie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
    //                                      String cookieName, String cookieValue, int cookieMaxage, String encodeString) {
    //    try {
    //        if (cookieValue == null) {
    //            cookieValue = "";
    //        } else {
    //            cookieValue = URLEncoder.encode(cookieValue, encodeString);
    //        }
    //        Cookie cookie = new Cookie(cookieName, cookieValue);
    //        if (cookieMaxage > 0) {
    //            cookie.setMaxAge(cookieMaxage);
    //        }
    //        if (null != request) {// 设置域名的cookie
    //            String domainName = getDomainName(request);
    //            System.out.println(domainName);
    //            if (!"localhost".equals(domainName)) {
    //                cookie.setDomain(domainName);
    //            }
    //        }
    //        cookie.setPath("/");
    //        response.addCookie(cookie);
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}
    private static final String getDomainName(HttpServletRequest request) {
        String domainName = null;
        // 通过request对象获取访问的url地址
        String serverName = request.getRequestURL().toString();
        if (serverName == null || serverName.equals("")) {
            domainName = "";
        } else {
            // 将url地下转换为小写
            serverName = serverName.toLowerCase();
            // 如果url地址是以http://开头  将http://截取
            if (serverName.startsWith("http://")) {
                serverName = serverName.substring(7);
            }
            int end = serverName.length();
            // 判断url地址是否包含"/"
            if (serverName.contains("/")) {
                //得到第一个"/"出现的位置
                end = serverName.indexOf("/");
            }

            // 截取
            serverName = serverName.substring(0, end);
            // 根据"."进行分割
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn
                domainName = domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName != null && domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }
}
