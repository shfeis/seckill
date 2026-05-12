package com.hspedu.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 这是RabbitMQ的配置类，可以创建队列、交换机(针对头路由模式)
 */
@Configuration
public class RabbitMQHeadersConfig {
    //定义队列名
    private static final String QUEUE01 = "queue_header01";
    private static final String QUEUE02 = "queue_header02";
    //定义交换机名
    private static final String EXCHANGE = "headerExchange";

    //把队列1放入到RabbitMQ服务中
    @Bean
    public Queue queue_header01() {
        //true表示队列持久化。Erlang语言自带Mnesia数据库，持久化就是将消息保存到该DB中,默认就是true
        return new Queue(QUEUE01);
    }

    //把队列2放入到RabbitMQ服务中
    @Bean
    public Queue queue_header02() {
        //true表示队列持久化。Erlang语言自带Mnesia数据库，持久化就是将消息保存到该DB中,默认就是true
        return new Queue(QUEUE02);
    }

    //把交换机放入到RabbitMQ服务中
    @Bean
    public HeadersExchange headerExchange() {
        return new HeadersExchange(EXCHANGE);
    }

    //将队列1绑定到交换机,并声明要匹配的k-v,以及指定匹配单个k-v(any)
    @Bean
    public Binding binding_header01() {
        //声明k-v，因为k-v可以有多个，所以要放在集合中
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "low");
        return BindingBuilder.bind(queue_header01()).to(headerExchange()).whereAny(map).match();
    }

    //将队列2绑定到交换机,并声明要匹配的k-v,以及指定必须匹配所有k-v(all)
    @Bean
    public Binding binding_header02() {
        //声明k-v，因为k-v可以有多个，所以要放在集合中
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "fast");
        return BindingBuilder.bind(queue_header02()).to(headerExchange()).whereAll(map).match();
    }
}
