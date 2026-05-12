package com.hspedu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 该类是RabbitMQ中的消息生产者
 */
@Service
@Slf4j
public class MQSender {
    @Resource
    private RabbitTemplate rabbitTemplate; //这是java操作Rabbit的工具组件

    //该方法指定将生产者与哪个交换机进行绑定(默认为广播模式，路由为空，即所以队列都可以接收到消息)
    public void sendFanout(Object msg) {
        log.info("发送消息-->" + msg);
        //指定要绑定的交换机名称
        rabbitTemplate.convertAndSend("fanoutExchanges", "", msg);
    }

    //该方法指定将生产者与哪个交换机进行绑定,发出消息给指定的交换机的路由下的队列
    public void sendDirect1(Object msg) {
        log.info("发送消息-->" + msg);
        //指定要绑定的交换机名称directExchange(queue.red是指定的路由)
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    //该方法指定将生产者与哪个交换机进行绑定,发出消息给指定的交换机的路由下的队列
    public void sendDirect2(Object msg) {
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }

    //该方法指定将生产者与哪个交换机进行绑定,发出消息给指定的交换机的路由下的队列
    public void sendTopic3(Object msg) {
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
    }

    //该方法指定将生产者与哪个交换机进行绑定,发出消息给指定的交换机的路由下的队列
    public void sendTopic4(Object msg) {
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "green.queue.green.message", msg);
    }

    //该方法指定将生产者与哪个交换机进行绑定,发出消息给相互匹配上的交换机的k-v下的队列
    public void sendHeader01(String msg) {
        log.info("发送消息-->" + msg);
        MessageProperties properties = new MessageProperties();
        //创建消息属性，用于匹配对应k-v的队列(模拟匹配上所有/all)
        properties.setHeader("color","red");
        properties.setHeader("speed","fast");
        //创建Message对象,它包含了要发送的k-v和消息本身,路由为""/空
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headerExchange","",message);
    }

    //该方法指定将生产者与哪个交换机进行绑定,发出消息给相互匹配上的交换机的k-v下的队列
    public void sendHeader02(String msg) {
        log.info("发送消息-->" + msg);
        MessageProperties properties = new MessageProperties();
        //创建消息属性，用于匹配对应k-v的队列(模拟匹配上一个/any)
        properties.setHeader("color","red");
        properties.setHeader("speed","normal");
        //创建Message对象,它包含了要发送的k-v和消息本身,路由为""/空
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headerExchange","",message);
    }
}
/* 路由就是一个断言机制，如果我传入的路由和队列绑定的路由格式匹配就可以进行消息传输 */