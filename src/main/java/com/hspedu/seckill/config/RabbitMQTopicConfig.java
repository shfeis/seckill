package com.hspedu.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这是RabbitMQ的配置类，可以创建队列、交换机、路由(针对主题模式)
 */
@Configuration
public class RabbitMQTopicConfig {
    //定义队列名
    private static final String QUEUE01 = "queue_topic01";
    private static final String QUEUE02 = "queue_topic02";
    //定义交换机名
    private static final String EXCHANGE = "topicExchange";
    //定义路由(模糊指定，为Topic模式，即主题模式、模糊的路由模式)
    private static final String ROUTING_KEY01 = "#.queue.#";
    private static final String ROUTING_KEY02 = "*.queue.#";

    //把队列1放入到RabbitMQ服务中
    @Bean
    public Queue queue_topic01() {
        //true表示队列持久化。Erlang语言自带Mnesia数据库，持久化就是将消息保存到该DB中,默认就是true
        return new Queue(QUEUE01);
    }
    //把队列2放入到RabbitMQ服务中
    @Bean
    public Queue queue_topic02() {
        //true表示队列持久化。Erlang语言自带Mnesia数据库，持久化就是将消息保存到该DB中,默认就是true
        return new Queue(QUEUE02);
    }
    //把交换机放入到RabbitMQ服务中
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    //将队列1绑定到交换机,并指定了路由(路由值为：#.queue.#)
    @Bean
    public Binding binding_topic01() {
        return BindingBuilder.bind(queue_topic01()).to(topicExchange()).with(ROUTING_KEY01);
    }
    //将队列2绑定到交换机,并指定了路由(路由值为：*.queue.#)
    @Bean
    public Binding binding_topic02() {
        return BindingBuilder.bind(queue_topic02()).to(topicExchange()).with(ROUTING_KEY02);
    }
}
