package com.hspedu.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 这是JavaBean对象，对应数据库表(客户秒杀订单表)字段的实体类
 */
@Data
@TableName("t_seckill_order")
public class SeckillOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;
}
