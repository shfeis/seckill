package com.hspedu.seckill.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该工具类完成一些校验工作，如：验证手机号码格式是否正确
 */
public class ValidatorUtil {
    //使用正则表达式去校验手机号码(以1开头，第二位3-9中选，后9位0-9中选，总共11位)
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[1][3-9][0-9]{9}$");

    //验证手机号码是否有效
    public static boolean isMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) { //判断传入的手机号码是否为字符串
            return false;
        }
        //如果为字符串就进行正则表达式校验
        Matcher matcher = MOBILE_PATTERN.matcher(mobile);
        return matcher.matches(); //返回boolean值
    }
}
