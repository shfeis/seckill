package com.hspedu.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 该工具类用于提供MD5对密码进行加密的方法
 */
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    //准备一个salt/盐,final表示常量
    private static final String SALT = "4tIY5VCX";

    //对密码进行第一次加密，返回中间值密码/midPass，加密公式：md5(password明文+盐)
    public static String inputPassToMidPass(String inputPass) {
        //将盐的第一位字符c、第7位字符T,拼接在password明文的首尾处
        String str = SALT.charAt(0) + inputPass + SALT.charAt(6);
        return md5(str);
    }

    //对密码进行第二次加密，返回数据库存储密码/dbPass，加密公式：md5(inputPassToMidPass()+盐)
    public static String midPassToDBPass(String midPass, String salt) {
        //将盐的第二位字符、第6位字符,拼接在第一次加密密码的首尾处.注：该方法中的盐由方法调用者提供
        String str = salt.charAt(1) + midPass + salt.charAt(5);
        return md5(str);
    }

    //提供方法，输入明文密码/inputPass，直接返回连续两次加密后的数据库存储密码/dbPass
    public static String inputPassToDBPass(String inputPass, String salt) {
        String midPass = inputPassToMidPass(inputPass);
        String dbPass = midPassToDBPass(midPass, salt);
        return dbPass;
    }
}
