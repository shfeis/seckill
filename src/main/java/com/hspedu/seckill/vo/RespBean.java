package com.hspedu.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 该类用于整合枚举类，因为在返回状态码和提示信息时可能会携带额外信息给客户端
 */
@Data //使用lombok的注解，自动生成get、set、toString等方法
@AllArgsConstructor //...，自动生成全参构造器
@NoArgsConstructor //..., 自动生成无参构造器
public class RespBean {
    private long code; //状态码
    private String message; //提示信息
    private Object obj; //额外信息

    //当程序执行成功后，携带数据返回给客户端
    public static RespBean success(Object data) {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), data);
    }
    //当程序执行成功后，不携带数据返回给客户端
    public static RespBean success() {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(),null);
    }
    //当程序执行失败后，携带数据返回给客户端
    public static RespBean error(RespBeanEnum respBeanEnum,Object data) {
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), data);
    }
    //当程序执行失败后，不携带数据返回给客户端
    public static RespBean error(RespBeanEnum respBeanEnum) {
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }
}
