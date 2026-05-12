package com.hspedu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 该类是RabbitMQ中的消息生产者
 * 在该项目中为解决MySQL事务隔离级别导致的线程堆积
 */
@Slf4j //允许日志输出
@Service //被当成Service层来使用，即目标方法去调用
public class MQSenderMessage {
    @Resource
    private RabbitTemplate rabbitTemplate;

    //该方法用于发送秒杀消息
    public void sendSeckillMassage(String message) {
        log.info("生产者发送的消息-->" + message);
        //指定发送给哪个交换机，指定路由，传入消息
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }
}
