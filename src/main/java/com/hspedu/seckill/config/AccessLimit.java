package com.hspedu.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这里自定义注解，做成了Redis计数器，完成对用户的限流防刷操作
 * 在拦截器中去实现自定义注解，完成义务代码
 */
@Retention(RetentionPolicy.RUNTIME) //该注解只有在程序运行时有效
@Target(ElementType.METHOD) //该注解作用于方法上
public @interface AccessLimit {
    int second(); //时间范围
    int maxCount(); //允许访问的最大次数
    boolean needLogin() default true; //用户是否要进行登录，true：要登录
}
