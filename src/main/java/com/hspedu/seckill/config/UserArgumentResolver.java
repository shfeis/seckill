package com.hspedu.seckill.config;

import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.UserService;
import com.hspedu.seckill.util.CookieUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这是解析器,比目标方法提前接收到用户请求信息，用于获取到user用户信息。为Controller层服务
 */
@Component //将解析器注入到容器中
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Resource
    private UserService userService;

    //判断当前要解析的参数类型是不是你需要的类型，如果返回false就不会执行下一个方法
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //获取调用者传入的类型/aClass
        Class<?> aClass = parameter.getParameterType();
        //判断aClass是否为User类型
        return aClass == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
//        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
//        String ticket = CookieUtil.getCookieValue(request, "userTicket");
//        if (!StringUtils.hasText(ticket)) {
//            return null;
//        }
//        //获取Redis中k=ticket的value值，即user(用户登录信息)
//        User user = userService.getUserByCookie(ticket, request, response);
//        return user;

        //上面的代码是用来获取用户信息并返回的，但是因为拦截器已经把用户信息放入到了ThreadLocal中，所有可以直接去获取user信息
        return UserContext.getUser();
    }
}
