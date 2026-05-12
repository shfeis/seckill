package com.hspedu.seckill.controller;

import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.UserService;
import com.hspedu.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 该类是控制器，用于返回用户信息，该方法是给Jemter做测试用的
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    //返回登录用户的信息
    @RequestMapping("/info")
    @ResponseBody //禁用视图，不进行请求转发
    public RespBean info(User user) {
        return RespBean.success(user);
    }

    //在DB、Redis中同步被修改的用户信息
    @RequestMapping("/updpwd")
    @ResponseBody
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        return userService.updatePassword(userTicket, password, request, response);
    }
}
