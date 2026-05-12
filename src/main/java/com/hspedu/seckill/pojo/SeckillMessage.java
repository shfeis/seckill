package com.hspedu.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这是一个javaBean对象
 * 用于封装service层的OrderServiceImpl类seckill方法需要的参数信息
 * 将封装的pojo对象由生产者放入到队列中被消费者取出。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private User user; //要秒杀商品的用户是谁
    private Long goodsId; //要秒杀的商品Id
}
