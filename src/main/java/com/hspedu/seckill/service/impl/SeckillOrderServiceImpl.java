package com.hspedu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hspedu.seckill.mapper.SeckillOrderMapper;
import com.hspedu.seckill.pojo.SeckillOrder;
import com.hspedu.seckill.service.SeckillOrderService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Service;

/**
 * 这是service层类，用于实现service层的接口(在目标方法调用service层接口的抽象方法时会直接定位到这里来)
 * 在MyBatis中该类可以直接实现service层的接口，而在MyBatis-plus中不仅要实现该接口，还要继承ServiceImpl<DAO层存放sql方法的接口,JavaBean>
 */
@MapperScan(basePackages = {"com.hspedu.springboot.mybatisplus.mapper"})//要扫描该路径下的DAO层接口
@Service //该类是一个service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

}
