package com.hspedu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.vo.LoginVo;
import com.hspedu.seckill.vo.RespBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *这是service层接口，用于实现DAO层的方法，和定义业务需求的抽象方法
 * 在MyBatis中该接口应该去定义抽象方法与DAO层的方法相呼应，而在MyBatis-plus中是继承IService类，
 * IService类中有很多的CRUD方法去操作数据库,如果提供的方法不满足业务需要就自定义抽象方法
 */
@Service //该接口是一个service
public interface UserService extends IService<User> {
        //自定义方法，完成用户登录(电话、密码)信息校验
       RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
        //客户端第二次请求，该方法提供根据Cookie返回用户信息的功能
        User getUserByCookie(String userTicket,HttpServletRequest request, HttpServletResponse response);
        //当用户修改个人信息时，该方法就更新DB中的用户表对应元素和Redis中的用户缓存信息(这里以用户修改密码为例)
        RespBean updatePassword(String userTicket,String password,HttpServletRequest request, HttpServletResponse response);
}
