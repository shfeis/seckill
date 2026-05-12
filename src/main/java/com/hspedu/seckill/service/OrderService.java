package com.hspedu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hspedu.seckill.pojo.Order;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.stereotype.Service;

/**
 * 这是service层接口，用于实现DAO层的方法，和定义业务需求的抽象方法
 * 在MyBatis中该接口应该去定义抽象方法与DAO层的方法相呼应，而在MyBatis-plus中是继承IService类，
 * IService类中有很多的CRUD方法去操作数据库,如果提供的方法不满足业务需要就自定义抽象方法
 */
@Service //该接口是一个service
public interface OrderService extends IService<Order> {
    //该方法会记录哪个用户秒杀了哪个商品,用于完成秒杀功能
    Order seckill(User user, GoodsVo goodsVo);
    /*
    该方法会在用户发出请求时生成唯一的秒杀路径/值(该值会返回给用户并保存到Redis中，让用户再次发出请求并携带该秒杀值)
    前端html发出请求的url地址是可以通过开发者模式直接看到的,一些非法人员会使用脚本高并发访问该目标方法。
    */
    String createPath(User user,Long goodsId);
    //该方法用于判断请求中的秒杀路径和Redis中的是否一致，如果一致就说明不是非法请求，允许继续执行，否则直接返回提示信息
    Boolean checkPath(User user,Long goodsId,String path);
    //该方法用于验证用户输入的验证码是否正确
    Boolean checkCaptcha(User user,Long goodsId,String captcha);
}
