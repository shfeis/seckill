package com.hspedu.seckill.config;

import com.hspedu.seckill.pojo.User;

/**
 * 这是工具类，用户保证线程安全的前提下去获取用户信息,在该项目中被拦截器使用
 */
public class UserContext {
    //ThreadLocal类可以让同一线程中的数据共享，不同线程数据分离。在并发时保证数据安全
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    //将用户信息添加到ThreadLocal
    public static void setUser(User user) {
        userHolder.set(user);
    }
    //获取用户信息
    public static User getUser() {
        return userHolder.get();
    }

}
