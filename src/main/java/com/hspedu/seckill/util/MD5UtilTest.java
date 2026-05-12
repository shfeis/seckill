package com.hspedu.seckill.util;

import org.junit.jupiter.api.Test;

/**
 * 该测试类，用于测试MD5Util类中提供的加密方法
 */
public class MD5UtilTest {
    //假设password明文/inputPass：“12345”
    private String password = "12345";

    @Test
    public void f1() {
        //输入明文密码，获取到中间值密码/midPass
        String midPass = MD5Util.inputPassToMidPass(password);
        System.out.println("中间值密码：" + midPass);

        //输入中间值密码和盐/salt，获取到数据库存储密码/dbPass
        String dbPass = MD5Util.midPassToDBPass(midPass, "hYLLSQ4x");
        System.out.println("数据库存储密码：" + dbPass);

        //输入明文密码，直接获取到数据库存储密码/dbPass
        String dbPass1 = MD5Util.inputPassToDBPass(password, "GfBiQk1X");
        System.out.println("数据库存储密码：" + dbPass1);
    }
}
