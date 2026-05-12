package com.hspedu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hspedu.seckill.pojo.SeckillOrder;

/**
 * 这是我的*Mapper接口，用于存放操作数据库的抽象方法--DAO层，用于定义sql操作的方法
 * 这里使用的是MyBatis-Plus，与MyBatis不同的是继承了BaseMapper类，该类提供了大量的单表CRUD方法
 * 如果提供的方法不满足业务需要就自定义抽象方法
 */
/* @Mapper 作用类似于applicationContext.xml，表示该接口会被扫描并注入，只有这样才可以使用类的方法
   如果有很多的DAO层的接口，各个都要使用@Mapper注解就很麻烦，可以在service层的实现类上使用@MapperScan来统一处理 */
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
}
