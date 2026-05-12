package com.hspedu.seckill.exception;

import com.hspedu.seckill.vo.RespBean;
import com.hspedu.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，接收和处理全局异常(当抛出全局异常时，该处理器自动调用)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理所有的异常
    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e) {
        //如果是全局异常，正常处理(因为自定义的全局异常只能处理RespBeanEnum类中包含的异常信息，其他异常就需要额外进行处理，所以就有BindException)
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        } else if (e instanceof BindException) { //如果是绑定异常就由RespBean类返回状态码等异常信息给客户端
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BING_ERROR);
            respBean.setMessage("参数校验异常~	：" + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR); //如果两个异常都不是，就直接返回枚举类中的状态码等提示信息
    }
}

