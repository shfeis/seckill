package com.hspedu.seckill.vo;

import com.hspedu.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 接收商品秒杀表、商品表 中的商品价格和库存等信息；为渲染商品页面做准备
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    /* 继承商品表、声明商品秒杀表字段，是为了整合两表中的所有字段 */
}
