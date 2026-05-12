package com.hspedu.seckill.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.hspedu.seckill.pojo.SeckillMessage;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.GoodsService;
import com.hspedu.seckill.service.OrderService;
import com.hspedu.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 该类是RabbitMQ中的消费者
 * 在该项目中为解决MySQL事务隔离级别导致的线程堆积
 */
@Service //被当成Service层来使用，即目标方法去调用
@Slf4j //允许使用日志输出信息
public class MQReceiverMessage {
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;

    //指定要接收哪个队列的消息，并完成用户秒杀下单的操作
    @RabbitListener(queues = "seckillQueue")
    public void queue(String message) {
        log.info("消费者接收到的消息-->" + message);
        //将String类型的消息转成POJO类型/SeckillMessage的对象，供秒杀方法使用.这里使用了hutool依赖
        SeckillMessage seckillMessage = JSONUtil.toBean(message, SeckillMessage.class);
        //得到要秒杀的用户
        User user = seckillMessage.getUser();
        //得到被秒杀的商品Id
        Long goodsId = seckillMessage.getGoodsId();
        //通过商品Id得到对应的商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //传入参数，调用秒杀方法
        orderService.seckill(user,goodsVo);
    }
}
