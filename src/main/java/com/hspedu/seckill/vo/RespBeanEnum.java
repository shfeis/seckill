package com.hspedu.seckill.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 这是一个枚举类,用于定义一些提示信息和状态码返回给客户端
 */
@Getter //使用lombok的注解，自动生成get方法
@ToString //...，自动生成toString方法
@AllArgsConstructor //...，自动生成全参构造器
public enum RespBeanEnum {
    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    //登录
    LOGIN_ERROR(500210, "用户id或者密码错误"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    BING_ERROR(500212, "参数绑定异常"),
    ENTRY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品每人限购一件"),
    PASSWORD_UPDATE_FAIL(500214, "密码更新失败"),
    REQUEST_ILLEGAL(500502,"请求非法"),
    SESSION_ERROR(500503,"用户信息有误"),
    SEK_KILL_WAIT(500504,"秒杀排队中..."),
    CAPTCHA_ERROR(500505,"验证码错误"),
    ACCESS_LIMIT_REACHED(500506,"访问频繁，请稍后再试..."),
    SEC_KILL_RETRY(500507,"本次抢购失败，请继续抢购..."),
    MOBILE_NOT_EXIST(500213, "查无此号,用户不存在");


    private final Integer code; //状态码
    private final String message; //提示信息
}
