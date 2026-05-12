package com.hspedu.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这是RabbitMQ的配置类，可以创建队列、交换机。
 * 在该项目中为解决MySQL事务隔离级别导致的线程堆积
 */
@Configuration //这是一个配置类
public class RabbitMQSeckillConfig {
    //定义队列名
    private static final String QUEUE = "seckillQueue";
    //定义交换机名
    private static final String EXCHANGE = "seckillExchange";
    //定义路由(#：表示匹配任意数量的单词)
    private static final String ROUTING_KEY = "seckill.#";

    //把队列放入到RabbitMQ服务中
    @Bean
    public Queue queue_seckill() {
        return new Queue(QUEUE);
    }

    //把交换机放入到RabbitMQ服务中
    @Bean
    public TopicExchange topicExchange_seckill() {
        return new TopicExchange(EXCHANGE);
    }

    //将队列绑定到交换机,并声明要匹配的路由
    @Bean
    public Binding binding_seckill() {
        return BindingBuilder.bind(queue_seckill()).to(topicExchange_seckill()).with(ROUTING_KEY);
    }
}
