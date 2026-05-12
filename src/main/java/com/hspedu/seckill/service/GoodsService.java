package com.hspedu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hspedu.seckill.pojo.Goods;
import com.hspedu.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *这是service层接口，用于实现DAO层的方法，和定义业务需求的抽象方法
 * 在MyBatis中该接口应该去定义抽象方法与DAO层的方法相呼应，而在MyBatis-plus中是继承IService类，
 * IService类中有很多的CRUD方法去操作数据库,如果提供的方法不满足业务需要就自定义抽象方法
 */
@Service //该接口是一个service
public interface GoodsService extends IService<Goods> {
    //商品列表
    List<GoodsVo> findGoodsVo();
    //商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
