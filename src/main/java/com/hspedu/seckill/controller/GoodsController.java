package com.hspedu.seckill.controller;

import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.service.GoodsService;
import com.hspedu.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 该类是控制器，用于执行商品页的方法
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    //完成登录后，调用该方法，进入到商品列表页
    //@CookieValue("userTicket") 通过Cookie名称获取到cookie值(ticket,这是service方法调用UUID工具类生成的Redis键值对K)
    //@CookieValue("userTicket") String ticket, HttpServletRequest request, HttpServletResponse response有点长，可以使用解析器提前获取到user用户信息，那么toList方法就只需要传入的参数为Model、User
    /*
    @Resource
    private UserService userService;
    @RequestMapping("/toList")
    public String toList(Model model, @CookieValue("userTicket") String ticket, HttpServletRequest request, HttpServletResponse response) {
        //如果后端无法获取到Cookie值就说明用户通过非法手段进入到该页面,就跳转到登录页面
        if(!StringUtils.hasText(ticket)) {
            return "login";
        }
        //通过ticket获取对应用户的session值，即user(用户登录信息)
        //User user = (User) session.getAttribute("ticket");
        User user = userService.getUserByCookie(ticket, request, response);
        //如果user为空，就说明用户虚构了一个Cookie值非法进入到该页面,就跳转到登录页面
        if(user == null) {
            return "login";
        }
        model.addAttribute("user",user); //将用户登录信息放入模板中可供下一个模板使用
        return "goodsList"; //调用视图，请求转发
    }
    */
    @Resource
    private GoodsService goodsService;
    @Resource
    private RedisTemplate redisTemplate;
    //加入手动渲染时需要的模板解析器
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    //    //这里使用了解析器(提前获取导入User用户信息)，但是该方法跳转到的页面会请求DB来显示数据。在高并发时容易压垮数据库
//    @RequestMapping("/toList")
//    public String toList(Model model, User user) {
//        //如果user为空，就说明用户虚构了一个Cookie值非法进入到该页面,就请求转发到登录页面
//        if (user == null) {
//            //调用视图，完成的请求转发的地址为：resources/templates/login.html
//            return "login";
//        }
//        //将用户登录信息、商品信息放入模板中可供下一个模板使用
//        model.addAttribute("goodsList", goodsService.findGoodsVo());
//        model.addAttribute("user", user);
//        return "goodsList"; //调用视图，请求转发
//    }

    //对toList方法进行修改，让商品列表页变成缓存存放到Redis中
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody //缓存到Redis中的页面是以字符串的形式保存的，所以需要禁用视图
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        //如果user为空，就说明用户虚构了一个Cookie值非法进入到该页面,就请求转发到登录页面
        if (user == null) {
            //调用视图，完成的请求转发的地址为：resources/templates/login.html
            return "login";
        }
        //先到redis中尝试获取商品列表页，如果没有就去DB中获取
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasText(html)) { //如果可以获取到就直接返回，不走数据库
            return html;
        }
        //将用户登录信息、商品信息放入模板中可供下一个模板使用
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        model.addAttribute("user", user);
        //如果可以走到以下代码，就说明Redis中没有指定的数据，就手动渲染并调用数据库/DB，再存放到Redis
        WebContext webContext = new WebContext(request, response, request.getServletContext(), response.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.hasText(html)) { //如果渲染成功，就将商品列表页保存到Redis,该页面超时时间为60秒
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html; //将商品列表页返回给客户端
    }

//    /*
//       当用户点击商品详情时，显示该商品详细信息。但是该方法跳转到的页面会请求DB来显示数据。在高并发时容易压垮数据库
//       {goodsId}是一个占位符，用于接收客户端传入的商品Id，只有这样才能显示指定商品的详细信息
//       @PathVariable表示goodsId参数来接收该占位符中的信息
//       如果{}中的名称不是参数名，就要使用@PathVariable(value = "goodsId")来定位
//     */
//    @RequestMapping("/toDetail/{goodsId}")
//    public String toDetail(Model model, User user, @PathVariable Long goodsId) {
//        if (user == null) {
//            return "login";
//        }
//        //将用户登录信息、指定商品详情信息放入模板中可供下一个模板使用
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goodsVo);
//        /*
//          secKillStatus表示秒杀状态：0(秒杀未开始) 1(秒杀进行中) 2(秒杀已经结束)
//          remainSeconds表示剩余秒数：大于0(还有多少秒开始秒杀) 0(秒杀进行中) -1(表示秒杀已经结束)
//         */
//        //开始时间
//        Date startDate = goodsVo.getStartDate();
//        //结束时间
//        Date endDate = goodsVo.getEndDate();
//        //当前时间
//        Date nowDate = new Date();
//        int secKillStatus = 0;
//        int remainSeconds = 0;
//        if (nowDate.before(startDate)) { //如果当前时间在开始时间之前，就返回true
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) { //如果当前时间在结束时间之后，就返回true
//            secKillStatus = 2;
//            remainSeconds = -1;
//        } else { //秒杀进行中
//            secKillStatus = 1;
//        }
//        //将指定商品的秒杀状态、剩余秒杀时间放入模板中可供下一个模板使用
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        return "goodsDetail";
//    }

    //对toDetail方法进行修改，让商品详情页变成缓存存放到Redis中
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody //缓存到Redis中的页面是以字符串的形式保存的，所以需要禁用视图
    public String toDetail(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            return "login";
        }
        //先到redis中尝试获取商品详情页，如果没有就去DB中获取
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (StringUtils.hasText(html)) { //如果可以获取到就直接返回，不走数据库
            return html;
        }
        //将用户登录信息、指定商品详情信息放入模板中可供下一个模板使用
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);
        /*
          secKillStatus表示秒杀状态：0(秒杀未开始) 1(秒杀进行中) 2(秒杀已经结束)
          remainSeconds表示剩余秒数：大于0(还有多少秒开始秒杀) 0(秒杀进行中) -1(表示秒杀已经结束)
         */
        //开始时间
        Date startDate = goodsVo.getStartDate();
        //结束时间
        Date endDate = goodsVo.getEndDate();
        //当前时间
        Date nowDate = new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)) { //如果当前时间在开始时间之前，就返回true
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) { //如果当前时间在结束时间之后，就返回true
            secKillStatus = 2;
            remainSeconds = -1;
        } else { //秒杀进行中
            secKillStatus = 1;
        }
        //将指定商品的秒杀状态、剩余秒杀时间放入模板中可供下一个模板使用
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        //如果可以走到以下代码，就说明Redis中没有指定的数据，就手动渲染并调用数据库/DB，再存放到Redis
        WebContext webContext = new WebContext(request, response, request.getServletContext(), response.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (StringUtils.hasText(html)) { //如果渲染成功，就将商品详情页保存到Redis,该页面超时时间为60秒
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html; //将商品详情页返回给客户端
    }
}
/* 这里能对user直接进行判断是因为使用了解析器来提前获取了user对象 */