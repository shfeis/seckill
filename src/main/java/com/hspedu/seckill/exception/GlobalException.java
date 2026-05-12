package com.hspedu.seckill.exception;

import com.hspedu.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局异常，该项目发生的所有异常都会抛到这里来
 */
@Data
@AllArgsConstructor //lombok的注解，全参构造器
@NoArgsConstructor //lombok的注解，无参构造器
public class GlobalException extends RuntimeException {
    //抛出的异常无非就是登录时的各种异常信息，所以使用该枚举类进行提示信息的返回
    private RespBeanEnum respBeanEnum;
}
