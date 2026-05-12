package com.hspedu.seckill.util;

import java.util.UUID;

/**
 * 生成uuid的工具类,自动生成的id号会成为session域中对应用户的session名字
 */
public class UUIDUtil {
    public static String uuid() {
        //默认生成的字符串格式：xxx-xxx-xxx-xxx,这里使用String方法，将"-"替换为""
        return UUID.randomUUID().toString().replace("-", "");
    }
}
