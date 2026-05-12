package com.hspedu.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.UserService;
import com.hspedu.seckill.util.CookieUtil;
import com.hspedu.seckill.vo.RespBean;
import com.hspedu.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 这是自定义拦截器，有两个作用：1，用于实现自定义注解的业务需求 2，将用户信息/User对象放入到ThreadLocal中
 */
@Component //这是一个组件，会注入到Spring容器中
public class AccessLimitInterceptor implements HandlerInterceptor { //实现该接口就表示为拦截器
    @Resource //做成组件，当成属性使用
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    /*
      1.该方法将用户信息通过UserContext类放到ThreadLocal中，保证线程安全
      2.该方法实现自定义注解的业务需求
    */
    @Override     //preHandle()在目标方法执行前被调用.拦截器调用的优先级大于解析器
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user); //将user存放到ThreadLocal

            //把handler转成HandlerMethod。 实现自定义注解
            HandlerMethod hm = (HandlerMethod) handler;
            //获取到目标方法的注解，即@AccessLimit
            AccessLimit annotation = hm.getMethodAnnotation(AccessLimit.class);
            if (annotation == null) { //为空，表示目标方法没有使用该注解，即不需要限流防刷的业务需求
                return true; //放行
            }
            //获取注解的值
            int second = annotation.second(); //时间范围
            int maxCount = annotation.maxCount(); //允许访问的最大次数
            boolean needLogin = annotation.needLogin(); //用户是否需要进行登录
            if (needLogin) { //为True,表示需要进行登录
                if (user == null) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }
            String uri = request.getRequestURI(); //获取到用户请求uri,即：/seckill/path
            String key = uri + ":" + user.getId();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (count == null) { //如果没有该k,就说明用户是第一次请求，创建k-v(初始值为1，过期时间为second秒)
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) { //在maxCount之前的请求都认为是正常访问
                valueOperations.increment(key); //调用Redis命令对指定值进行加一操作
            } else { //在maxCount之后的请求，都表示为刷接口操作
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    //该方法通过Cookie域中的userTicket(电话、密码)值，到DB中获取到用户信息/user
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (!StringUtils.hasText(ticket)) {
            return null; //说明该用户没有登录，因为客户端无法获取到Cookie值
        }
        return userService.getUserByCookie(ticket, request, response);
    }

    //该方法用于辅助preHandle()，因为它返回的数据类型为布尔型，无法返回RespBean类型的提示信息。该方法构建返回对象以流/response的形式返回
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        //设置流的文件类型与编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter output = response.getWriter();
        //构建RespBean对象，设置提示信息
        RespBean error = RespBean.error(respBeanEnum);
        //发送提示信息，以String类型发送，故需要转型
        output.write(new ObjectMapper().writeValueAsString(error));
        output.flush(); //刷新流
        output.close(); //关闭流
    }
}
