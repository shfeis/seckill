package com.hspedu.seckill.vo;

import com.hspedu.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 用来接收用户登录时发送的信息(手机号码，密码)；为保存用户信息到Redis中做准备
 *
 */
@Data
public class LoginVo {
    //下注解的使用，就规定了客户端登录信息的要求，就可以不在service层进行登录信息校验
    @NotNull //不允许为空
    @IsMobile //自定义注解，必须填写手机号
    private String mobile;
    @NotNull
    @Length(min = 32) //规定密码字符串的长度范围(最小32位)
    private String password;
}
