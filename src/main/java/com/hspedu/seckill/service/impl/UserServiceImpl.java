package com.hspedu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hspedu.seckill.exception.GlobalException;
import com.hspedu.seckill.exception.GlobalExceptionHandler;
import com.hspedu.seckill.mapper.UserMapper;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.UserService;
import com.hspedu.seckill.util.CookieUtil;
import com.hspedu.seckill.util.MD5Util;
import com.hspedu.seckill.util.UUIDUtil;
import com.hspedu.seckill.util.ValidatorUtil;
import com.hspedu.seckill.vo.LoginVo;
import com.hspedu.seckill.vo.RespBean;
import com.hspedu.seckill.vo.RespBeanEnum;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

/**
 * 这是service层类，用于实现service层的接口(在目标方法调用service层接口的抽象方法时会直接定位到这里来)
 * 在MyBatis中该类可以直接实现service层的接口，而在MyBatis-plus中不仅要实现该接口，还要继承ServiceImpl<DAO层存放sql方法的接口,JavaBean>
 */
@MapperScan(basePackages = {"com.hspedu.springboot.mybatisplus.mapper"})//要扫描该路径下的DAO层接口
@Service //该类是一个service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource //将DAO层的类当成一个属性来使用
    private UserMapper userMapper;
    @Resource  //该RedisTemplate类是RedisConfig类中获取到的
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(@Valid LoginVo loginVo,  //@Valid表示该参数会被校验器进行校验
                            HttpServletRequest request,
                            HttpServletResponse response) {
        //接收到用户传入的手机号、密码(中间值密码/midPass)
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//   ========因为IsMobileValidator校验器可以对数据进行校验，所以可以去除以下代码==========
//        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)) { //如果手机或密码有一个为空就不玩了
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR); //调用枚举类的方法，返回状态码、提示信息
//        }
//        if (!ValidatorUtil.isMobile(mobile)) { //工具类，校验手机号.如果手机格式不正确就不玩了
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        //查询数据库，看看用户是否存在(根据电话查询到该用户的所有信息，CRUD方法由MyBatisPlus提供)
        User user = userMapper.selectById(mobile);
        if (user == null) {
            //因为全局异常封装了RespBeanEnum类，所以可以使用全局异常来处理状态码等提示信息
            //return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST);
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        //如果用户存在，就要对密码进行第二次加密后与数据库存储密码进行比较;加密用的盐由数据库提供user.getSlat()
        if (!MD5Util.midPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            System.out.println("数据库密码：" + MD5Util.midPassToDBPass(password, user.getSlat()));
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        //如果可以执行到以下代码，就表示用户信息正确，允许登录
        //将用户登录信息保存到Redis中，用户下次就可以直接登录。ticket是uuid为用户随机生成的票据id，user是对应用户信息(包括电话、密码)
        String ticket = UUIDUtil.uuid();
        //将用户信息保存到session域中（代码会将session统一存放到Redis中，这不是我想要的自定义操作Redis库，所有注销掉）
        //request.getSession().setAttribute(ticket,user);
        //将用户信息保存到Redis库中(其中键值对的Key和value由我决定)
        redisTemplate.opsForValue().set("user:" + ticket, user);
        //将ticket存放到客户端的Cookie中
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasText(userTicket)) { //如果无法获取到Cookie就返回空
            return null;
        }
        //根据拼接的K，返回Redis中对应的value值(根据票据返回用户信息)
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        //如果用户第二次登录成功，刷新客户端Cookie，防止Cookie过期
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        //传入用户手机号码,获取用户信息
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) { //如果无法获取到用户信息，就抛出自定义的全局异常
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        //修改用户信息，设置新的密码
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int i = userMapper.updateById(user);
        if (i == 1) { //如果为true，就说明DB用户表信息修改成功
            //这时就应该删除Redis中对应的用户信息，然后将更新后的数据再添加到Redis缓存中(删除缓存让用户重新登录自然就可以更新数据)
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        //否则就是密码更新失败，调用枚举类，返回提示信息
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
