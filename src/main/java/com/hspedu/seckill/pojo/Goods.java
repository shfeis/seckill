package com.hspedu.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 这是JavaBean对象，对应数据库表(商品表)字段的实体类
 */
@Data
@TableName("t_goods")
public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;
    //商品id,指定该属性对应的表字段，并且为自增长的
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String goodsName;
    //商品标题
    private String goodsTitle;
    //商品图片
    private String goodsImg;
    //商品详情
    private String goodsDetail;
    //商品价格
    private BigDecimal goodsPrice;
    //商品库存
    private Integer goodsStock;
}
