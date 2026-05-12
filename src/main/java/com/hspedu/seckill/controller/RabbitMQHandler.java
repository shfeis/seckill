package com.hspedu.seckill.controller;

import com.hspedu.seckill.rabbitmq.MQSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 该控制器用于操作RabbitMQ的生产者去发送消息
 */
@Controller
public class RabbitMQHandler {
    @Resource
    private MQSender mqSender; //将生产者做成组件，当成属性使用

    //调用生产者发送消息
    @RequestMapping("/mq/fanout")
    @ResponseBody //禁用视图，返回字符串
    public void fanout() {
        mqSender.sendFanout("hello,jack");
    }

    //调用生产者发送消息
    @RequestMapping("/mq/direct01")
    @ResponseBody //禁用视图，返回字符串
    public void direct01() {
        mqSender.sendDirect1("hello,smith");
    }

    //调用生产者发送消息
    @RequestMapping("/mq/direct02")
    @ResponseBody //禁用视图，返回字符串
    public void direct02() {
        mqSender.sendDirect2("hello,tomcat");
    }

    //调用生产者发送消息
    @RequestMapping("/mq/topic01")
    @ResponseBody //禁用视图，返回字符串
    public void topic01() {
        mqSender.sendTopic3("hello,red");
    }

    //调用生产者发送消息
    @RequestMapping("/mq/topic02")
    @ResponseBody //禁用视图，返回字符串
    public void topic02() {
        mqSender.sendTopic4("hello,green");
    }

    //调用生产者发送消息
    @RequestMapping("/mq/header01")
    @ResponseBody //禁用视图，返回字符串
    public void header01() {
        mqSender.sendHeader01("hello ABC");
    }

    //调用生产者发送消息
    @RequestMapping("/mq/header02")
    @ResponseBody //禁用视图，返回字符串
    public void header02() {
        mqSender.sendHeader02("hello HSP");
    }
}
