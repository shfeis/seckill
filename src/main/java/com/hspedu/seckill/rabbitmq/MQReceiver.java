package com.hspedu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 该类是RabbitMQ中的消息接收者/消费者
 */
@Service //被当成Service层来使用，即目标方法去调用
@Slf4j //允许使用日志输出信息
public class MQReceiver {

    //该方法用于接收消息(该方法能接收到queue_fanout01和queue_fanout02队列的消息，因为它使用了广播模式)
    @RabbitListener(queues = "queue_fanout01") //指定该消费者处理哪个队列的消息
    public void receive1(Object msg) {
        log.info("接收到消息-->" + msg);
    }

    //该方法用于接收消息(该方法能接收到queue_fanout01和queue_fanout02队列的消息，因为它使用了广播模式)
    @RabbitListener(queues = "queue_fanout02") //指定该消费者处理哪个队列的消息
    public void receive2(Object msg) {
        log.info("接收到消息-->" + msg);
    }

    //该方法用于接收消息(该方法只能接收到queue_direct01队列的消息，因为它使用了路由模式)
    @RabbitListener(queues = "queue_direct01") //指定该消费者处理哪个队列的消息
    public void queue_direct1(Object msg) {
        log.info("queue_direct01接收到消息-->" + msg);
    }

    //该方法用于接收消息(该方法只能接收到queue_direct02队列的消息，因为它使用了路由模式)
    @RabbitListener(queues = "queue_direct02") //指定该消费者处理哪个队列的消息
    public void queue_direct2(Object msg) {
        log.info("queue_direct02接收到消息-->" + msg);
    }

    //该方法用于接收消息
    @RabbitListener(queues = "queue_topic01") //指定该消费者处理哪个队列的消息
    public void queue_topic1(Object msg) {
        log.info("queue_topic1接收到消息-->" + msg);
    }

    //该方法用于接收消息
    @RabbitListener(queues = "queue_topic02") //指定该消费者处理哪个队列的消息
    public void queue_topic2(Object msg) {
        log.info("queue_topic2接收到消息-->" + msg);
    }

    //该方法用于接收消息
    @RabbitListener(queues = "queue_header01") //指定该消费者处理哪个队列的消息
    public void queue_header1(Message message) {
        log.info("queue_header1接收到消息对象-->" + message);
        log.info("消息为-->" + new String(message.getBody()));
    }

    //该方法用于接收消息
    @RabbitListener(queues = "queue_header02") //指定该消费者处理哪个队列的消息
    public void queue_header2(Message message) {
        log.info("queue_header2接收到消息对象-->" + message);
        log.info("消息为-->" + new String(message.getBody()));
    }
}
