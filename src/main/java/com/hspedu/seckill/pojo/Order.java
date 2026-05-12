package com.hspedu.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 这是JavaBean对象，对应数据库表(客户所有订单表/包括秒杀和非秒杀)字段的实体类
 */
@Data
@TableName("t_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long goodsId;
    private Long deliveryAddrId;
    private String goodsName;
    private Integer goodsCount;
    private BigDecimal goodsPrice;
    //订单渠道1pc，2Android，3ios
    private Integer orderChannel;
    //订单状态：0 新建未支付 1 已支付 2 已发货 3 已收货 4 已退款 5 已完成
    private Integer status;
    private Date createDate;
    private Date payDate;
}
