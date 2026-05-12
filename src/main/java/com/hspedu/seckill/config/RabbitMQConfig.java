package com.hspedu.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这是RabbitMQ的配置类，可以创建队列、交换机、路由(针对广播模式和路由模式)
 */
@Configuration
public class RabbitMQConfig {
    //定义队列名
    private static final String QUEUE1 = "queue_fanout01";
    private static final String QUEUE2 = "queue_fanout02";
    private static final String QUEUE_DIRECT1 = "queue_direct01";
    private static final String QUEUE_DIRECT2 = "queue_direct02";
    //定义交换机名
    private static final String EXCHANGES = "fanoutExchanges";
    private static final String EXCHANGE_DIRECT = "directExchange";
    //定义路由(路由模式，如果没有声明路由就为默认的广播模式)
    private static final String ROUTING_KEY01 = "queue.red";
    private static final String ROUTING_KEY02 = "queue.green";

    //把队列1放入到RabbitMQ服务中
    @Bean
    public Queue queue1() {
        //true表示队列持久化。Erlang语言自带Mnesia数据库，持久化就是将消息保存到该DB中
        return new Queue(QUEUE1, true);
    }

    //把队列2放入到RabbitMQ服务中
    @Bean
    public Queue queue2() {
        //即使不写true，该方法默认也是True
        return new Queue(QUEUE2);
    }

    //把队列3放入到RabbitMQ服务中
    @Bean
    public Queue queue_direct1() {
        //即使不写true，该方法默认也是True
        return new Queue(QUEUE_DIRECT1);
    }

    //把队列4放入到RabbitMQ服务中
    @Bean
    public Queue queue_direct2() {
        //即使不写true，该方法默认也是True
        return new Queue(QUEUE_DIRECT2);
    }

    //把交换机1放入到RabbitMQ服务中
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(EXCHANGES);
    }

    //把交换机2放入到RabbitMQ服务中
    @Bean
    public DirectExchange exchange_direct() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    //将队列1绑定到交换机
    @Bean
    public Binding binding01() {
        return BindingBuilder.bind(queue1()).to(exchange());
    }

    //将队列2绑定到交换机
    @Bean
    public Binding binding02() {
        return BindingBuilder.bind(queue2()).to(exchange());
    }

    //将队列3绑定到交换机,并指定了路由(路由值为：queue.red)
    @Bean
    public Binding binding_direct1() {
        return BindingBuilder.bind(queue_direct1()).to(exchange_direct()).with(ROUTING_KEY01);
    }

    //将队列4绑定到交换机,并指定了路由(路由值为：queue.green)
    @Bean
    public Binding binding_direct2() {
        return BindingBuilder.bind(queue_direct2()).to(exchange_direct()).with(ROUTING_KEY02);
    }
}
