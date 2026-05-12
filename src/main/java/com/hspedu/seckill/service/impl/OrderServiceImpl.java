package com.hspedu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hspedu.seckill.mapper.OrderMapper;
import com.hspedu.seckill.pojo.Order;
import com.hspedu.seckill.pojo.SeckillGoods;
import com.hspedu.seckill.pojo.SeckillOrder;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.OrderService;
import com.hspedu.seckill.service.SeckillGoodsService;
import com.hspedu.seckill.service.SeckillOrderService;
import com.hspedu.seckill.util.MD5Util;
import com.hspedu.seckill.util.UUIDUtil;
import com.hspedu.seckill.vo.GoodsVo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 这是service层类，用于实现service层的接口(在目标方法调用service层接口的抽象方法时会直接定位到这里来)
 * 在MyBatis中该类可以直接实现service层的接口，而在MyBatis-plus中不仅要实现该接口，还要继承ServiceImpl<DAO层存放sql方法的接口,JavaBean>
 */
@MapperScan(basePackages = {"com.hspedu.springboot.mybatisplus.mapper"})//要扫描该路径下的DAO层接口
@Service //该类是一个service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SeckillOrderService seckillOrderService;

    //该方法会记录哪个用户秒杀了哪个商品,即完成秒杀功能
    @Transactional //该方法为一个事务
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        //查询秒杀商品的库存数量，对值进行-1
        //去数据库(t_seckill_goods)中通过goods_id返回库存数量(stock_count)
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
//        //对DB商品表库存进行-1，然后添加订单表元素，因为该操作不具备原子性，所以在高并发时商品-1反而会生成多个订单，导致超卖
//        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
//        seckillGoodsService.updateById(seckillGoods);

        /*
         * 该操作利用了Mysql事务隔离级别，DB默认的隔离级别为：Repeatable_read(可重复读)
         * 该隔离级别在执行update语句时会锁定要修改的行，即在高并发时，只能一个个的进行修改，具有原子性。就不会导致超卖
         * 该行代码的意思是：对指定id的商品[eq("goods_id", goodsVo.getId())]库存进行减一操作[setSql("stock_count=stock_count-1")]，直到库存为0结束[ge("stock_count", 0)]
         *
         * 但是该操作会导致请求全部进入到DB中，容易压垮DB.因此可以在控制器类中使用Redis先预存商品库存，让Redis中的库存先进行减一操作，由于Redis本身具有原子性、Redis库存与DB库存数据互不影响，不会导致多减。
         * 如果Redis库存减一大于等于0就允许进入到该方法中进行操作，这样就大大减少了DB请求数量
         *
         * 因为MySQL的事务隔离级别，所有要生成秒杀订单的请求会排队等待一个个的进行操作，但这会造成线程堆积，容易导致请求超时、内存资源紧张。
         * 因此使用秒杀的异步请求来解决，即当客户端发出请求后，Controller/RabbitMQ生产者的调用类 立刻返回“正在秒杀中...”的提示信息，
         * 之后由RabbitMQ的消费者调用该方法完成秒杀操作，将最终结果放入到队列中，前端通过轮询算法在队列中获取最终结果
         */
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=stock_count-1").eq("goods_id", goodsVo.getId()).gt("stock_count", 0));
        if (!update) { //当返回false时，就表示商品库存为0，结束添加商品订单。之后的请求都会进入到该判断语句中
            //把秒杀失败的信息记录到Redis中，让
            return null;
        }
        //生成所有商品订单(包括秒杀和非秒杀)
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L); //对收货地址进行初始化
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1); //订单渠道
        order.setStatus(0); //未支付
        order.setCreateDate(new Date()); //订单生成时间为当前时间
        //保存订单到数据库/order
        orderMapper.insert(order);

        //生成秒杀商品订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrderService.save(seckillOrder);
        //将秒杀商品订单在Redis缓存中也存放一份，因为程序判断用户是否复购是通过秒杀订单表来判断的，反复操作DB会影响性能
        //k--> order:用户id:商品id
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), seckillOrder);
        //返回所有商品订单
        return order;
    }

    //生成秒杀路径，返回给客户端
    @Override
    public String createPath(User user, Long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid());
        //将秒杀路径保存到Redis中，过期时间为60S
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }

    //判断秒杀路径是否一致. path为createPath()生成秒杀路径，由客户端传入
    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }
        Object redisPath = redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return redisPath.equals(path);
    }

    //验证用户输入的验证码是否与Redis中保存的一致
    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return false;
        }
        //从Redis取出验证码
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
