package com.hspedu.seckill.controller;

import com.hspedu.seckill.service.UserService;
import com.hspedu.seckill.vo.LoginVo;
import com.hspedu.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 该类是控制器，用于执行service层的方法(即用户登录页面的方法)
 */
@Controller
@RequestMapping("/login") //这是一个控制器，用于接收、响应http请求，存放目标方法
public class LoginController {
    @Resource
    private UserService userService;

    //编写方法，跳转到用户登录首页
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login"; //因为没有使用@ResponseBody，所有是使用视图，即请求转发(resources/templates/login.html)
    }

    //编写方法，接收用户登录请求，完成信息校验
    @RequestMapping("/doLogin")
    @ResponseBody //不使用视图，而是直接返回数据
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        return userService.doLogin(loginVo, request, response);
    }
}
