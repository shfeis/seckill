package com.hspedu.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 这是JavaBean对象，对应数据库表(用户表)字段的实体类
 */
@Data //调用lombok的注解，自动生成Get、Set、toString等方法
@TableName("seckill_user") //将该javabean与指定表名对应，如果该类名与表名一致就不需要该注解
public class User implements Serializable { //实现Serializable接口，进行序列化
    private static final long serialVersionUID = 1L;
    //用户ID,手机号码.该注解是MyBatisPlus的，指定id字段为主键，IdType.ASSIGN_ID表示值由程序自动生成
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    //用户名
    private String nickname;
    //数据库存储密码，MD5(MD5(pass明文+固定salt)+salt)
    private String password;
    //slat/盐，用于密码加密
    private String slat;
    //头像
    private String head;
    //注册时间
    private Date registerDate;
    //最后一次登录时间
    private Date lastLoginDate;
    //登录次数
    private Integer loginCount;
}
