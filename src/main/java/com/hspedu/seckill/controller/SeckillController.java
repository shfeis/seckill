package com.hspedu.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.hspedu.seckill.config.AccessLimit;
import com.hspedu.seckill.pojo.Order;
import com.hspedu.seckill.pojo.SeckillMessage;
import com.hspedu.seckill.pojo.SeckillOrder;
import com.hspedu.seckill.pojo.User;
import com.hspedu.seckill.rabbitmq.MQSenderMessage;
import com.hspedu.seckill.service.GoodsService;
import com.hspedu.seckill.service.OrderService;
import com.hspedu.seckill.util.UUIDUtil;
import com.hspedu.seckill.vo.GoodsVo;
import com.hspedu.seckill.vo.RespBean;
import com.hspedu.seckill.vo.RespBeanEnum;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 该类是控制器，用于执行service层的方法(即商品详情页的方法)
 */
@Controller
@RequestMapping("/seckill") //这是一个控制器，用于接收、响应http请求，存放目标方法
public class SeckillController implements InitializingBean {
    @Resource
    private GoodsService goodsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private OrderService orderService;
    //该集合用于记录秒杀商品是否还有库存
    private HashMap<Long, Boolean> entryStockMap = new HashMap<>();
    @Resource
    private MQSenderMessage mqSenderMessage;
    @Resource
    private RedisScript script;
    //处理用户秒杀/抢购商品的请求(没有使用Redis预存商品库存机制)
//    @RequestMapping("/doSeckill")
//    public String doSeckill(Model model, User user, Long goodsId) {
//        if (user == null) {
//            return "login";
//        }
//        //将用户信息放入模板中，供下一个模板使用
//        model.addAttribute("user", user);
//        //获取指定的商品详细信息
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断指定的商品是否还有库存剩余
//        if (goodsVo.getStockCount() < 1) {
//            //将库存不足的提示信息放入到模板中,跳转到错误页面
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        //判断指定的商品是否存在复购行为(如果秒杀订单表中存在该用户就说明是复购，去DB表查询)
////        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
////        if (seckillOrder != null) {
////            //将不可复购的提示信息放入到模板中,跳转到错误页面
////            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
////            return "seckillFail";
////        }
//
//        //判断指定的商品是否存在复购行为(如果秒杀订单表中存在该用户就说明是复购，去Redis缓存查询)
//        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        if (o != null) {
//            //将不可复购的提示信息放入到模板中,跳转到错误页面
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "seckillFail";
//        }
//        //将秒杀订单在 客户所有订单表 中生成一份
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//        return "orderDetail";
//    }
    /* ------------------------------------------------------------------------------------------------------------- */
//    //处理用户秒杀/抢购商品的请求(使用Redis预存商品库存机制)
//    @RequestMapping("/doSeckill")
//    public String doSeckill(Model model, User user, Long goodsId) {
//        if (user == null) {
//            return "login";
//        }
//        //将用户信息放入模板中，供下一个模板使用
//        model.addAttribute("user", user);
//        //获取指定的商品详细信息
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断指定的商品是否还有库存剩余
//        if (goodsVo.getStockCount() < 1) {
//            //将库存不足的提示信息放入到模板中,跳转到错误页面
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        //判断指定的商品是否存在复购行为(如果秒杀订单表中存在该用户就说明是复购，去Redis缓存查询)
//        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        if (o != null) {
//            //将不可复购的提示信息放入到模板中,跳转到错误页面
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "seckillFail";
//        }
//
//        //对map集合进行判断，如果商品在map集合中没有库存就直接返回，不访问Redis中的预存储的库存
//        Boolean b = entryStockMap.get(goodsId);
//        if (b) {
//            //将库存不足的提示信息放入到模板中,跳转到错误页面
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        /*
//           一、对Redis商品库存进行预减一(decrement方法具有原子性)，虽然该操作明显降低了DB的并发数量，但是Redis却会接收所有的请求，
//           频繁的修改操作会增加Redis的压力。
//           二、使用内存标记的设计思路来减少Redis的并发数量：
//            1.在本机JVM的map集合中记录所有商品的库存数量
//            2.在执行Redis预减库存时，先到map中查询指定商品是否还有库存；如果有就执行预减，如果没有就直接返回，禁止请求
//            3.本机JVM内存一定快于非本机Redis内存，所有JVM内存一定会优先执行
//        */
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
//        if (decrement < 0) { //小于0说明商品已经卖完了
//            //当指定的商品没有库存时，设置Map集合的value为true,即没有库存
//            entryStockMap.put(goodsId, true);
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId); //将库存恢复为0，库存值就不会为负数了。纯好看可以不要
//            //将库存不足的提示信息放入到模板中,跳转到错误页面
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        //将秒杀订单在 客户所有订单表 中生成一份
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//        return "orderDetail";
//    }
    //----------------------------------------------------------------------------------------
//    //处理用户秒杀/抢购商品的请求(使用Redis预存商品库存机制、操作RabbitMQ的生产者)
//    @RequestMapping("/{path}/doSeckill")
//    @ResponseBody
//    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {
//        //判断用户是否完成登录操作
//        if (user == null) {
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//        //校验用户携带的秒杀路径是否与Redis中的一致
//        Boolean b = orderService.checkPath(user, goodsId, path);
//        //如果不一致就说明是用户使用脚本进行的非法请求
//        if (!b) {
//            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
//        }
//        //获取指定的商品详细信息
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断指定的商品是否还有库存剩余
//        if (goodsVo.getStockCount() < 1) {
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
//        }
//        //判断指定的商品是否存在复购行为(如果秒杀订单表中存在该用户就说明是复购，去Redis缓存查询)
//        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        if (o != null) {
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//
//        //对map集合进行判断，如果商品在map集合中没有库存就直接返回，不访问Redis中的预存储的库存
//        Boolean map = entryStockMap.get(goodsId);
//        if (map) {
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
//        }
//        /*
//           一、对Redis商品库存进行预减一(decrement方法具有原子性)，虽然该操作明显降低了DB的并发数量，但是Redis却会接收所有的请求，
//           频繁的修改操作会增加Redis的压力。
//           二、使用内存标记的设计思路来减少Redis的并发数量：
//            1.在本机JVM/java程序 的map集合中记录所有商品的库存数量
//            2.在执行Redis预减库存时，先到map中查询指定商品是否还有库存；如果有就执行预减，如果没有就直接返回，禁止请求
//            3.本机JVM内存一定快于非本机Redis内存，所有JVM内存一定会优先执行
//        */
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
//        if (decrement < 0) { //小于0说明商品已经卖完了
//            //当指定的商品没有库存时，设置Map集合的value为true,即没有库存
//            entryStockMap.put(goodsId, true);
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId); //将库存恢复为0，库存值就不会为负数了。纯好看可以不要
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
//        }
//        //向RabbitMQ队列发送秒杀请求/消息，实现异步秒杀请求。 使用hutool依赖，将POJO对象转成String,因为生产者发送的消息类型为String类型
//        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
//        mqSenderMessage.sendSeckillMassage(JSONUtil.toJsonStr(seckillMessage));
//        return RespBean.error(RespBeanEnum.SEK_KILL_WAIT);
//    }

    //处理用户秒杀/抢购商品的请求(操作RabbitMQ的生产者、使用Redis分布式锁)
    @RequestMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {
        //判断用户是否完成登录操作
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //校验用户携带的秒杀路径是否与Redis中的一致
        Boolean b = orderService.checkPath(user, goodsId, path);
        //如果不一致就说明是用户使用脚本进行的非法请求
        if (!b) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //获取指定的商品详细信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断指定的商品是否还有库存剩余
        if (goodsVo.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }
        //判断指定的商品是否存在复购行为(如果秒杀订单表中存在该用户就说明是复购，去Redis缓存查询)
        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
        if (o != null) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }

        //对map集合进行判断，如果商品在map集合中没有库存就直接返回，不访问Redis中的预存储的库存
        Boolean map = entryStockMap.get(goodsId);
        if (map) {
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }
        /*
           一、对Redis商品库存进行预减一(decrement方法具有原子性)，虽然该操作明显降低了DB的并发数量，但是Redis却会接收所有的请求，
           频繁的修改操作会增加Redis的压力。
           二、使用内存标记的设计思路来减少Redis的并发数量：
            1.在本机JVM/java程序 的map集合中记录所有商品的库存数量
            2.在执行Redis预减库存时，先到map中查询指定商品是否还有库存；如果有就执行预减，如果没有就直接返回，禁止请求
            3.本机JVM内存一定快于非本机Redis内存，所有JVM内存一定会优先执行

            Redis的减一操作本身就具有原子性，可以很好的降低DB的并发数量。但我注销该代码是为了演示使用Redis分布式锁来降低DB的并发数量
        */
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
//        if (decrement < 0) { //小于0说明商品已经卖完了
//            //当指定的商品没有库存时，设置Map集合的value为true,即没有库存
//            entryStockMap.put(goodsId, true);
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId); //将库存恢复为0，库存值就不会为负数了。纯好看可以不要
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
//        }

        //使用Redis分布式锁来降低DB的并发数量,并通过RedisConfig配置类来调用lua脚本
        String uuid = UUID.randomUUID().toString();
        //设置一个分布式锁 key=lock,value=uuid. 该锁的超时时间为3秒(TimeUnit.SECONDS表示以秒为单位)
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (lock) { //为true,说明锁设置成功(相当于请求抢夺到锁，允许秒杀)
            Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
            if (decrement < 0) { //小于0说明商品已经卖完了
                //当指定的商品没有库存时，设置Map集合的value为true,即没有库存
                entryStockMap.put(goodsId, true);
                redisTemplate.opsForValue().increment("seckillGoods:" + goodsId); //将库存恢复为0，库存值就不会为负数了。纯好看可以不要
                //使用Redis+Lua脚本来释放锁
                redisTemplate.execute(script, Arrays.asList("lock"), uuid);
                return RespBean.error(RespBeanEnum.ENTRY_STOCK);
            }
            //使用Redis+Lua脚本来释放锁
            redisTemplate.execute(script, Arrays.asList("lock"), uuid);
        } else { //说明锁设置失败(相当于请求没抢夺到锁)
            return RespBean.error(RespBeanEnum.SEC_KILL_RETRY);
        }

        //向RabbitMQ队列发送秒杀请求/消息，实现异步秒杀请求。 使用hutool依赖，将POJO对象转成String,因为生产者发送的消息类型为String类型
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.sendSeckillMassage(JSONUtil.toJsonStr(seckillMessage));
        return RespBean.error(RespBeanEnum.SEK_KILL_WAIT);
    }

    //自动生成秒杀路径返回给客户端,并且校验用户输入的验证码是否与Redis中的一致(当用户点击秒杀时会先调用该方法生成秒杀路径后，如果验证码一致，就重定向到doSeckill()/秒杀方法)
    @RequestMapping("/path")
    @ResponseBody //禁用视图，返回对象
    @AccessLimit(second = 5, maxCount = 5, needLogin = true) //使用自定义注解
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        /*
         加入Redis计数器，完成对用户的限流防刷操作
         在Redis中创建k-v,k：uri:userId  v：用户请求次数。在5秒内如果value值>=5,就说明在进行刷接口操作

         把业务代码放在这里会导致代码冗余，如果Redis计数器做成自定义注解@AccessLimit 就可以提高可用性
        */
//        String uri = request.getRequestURI(); //获取到用户请求uri,即：/seckill/path
//        String key = uri + ":" + user.getId();
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        Integer count = (Integer) valueOperations.get(key);
//        if (count == null) { //如果没有该k,就说明用户是第一次请求，创建k-v(初始值为1，过期时间为5秒)
//            valueOperations.set(key, 1, 5, TimeUnit.SECONDS);
//        } else if (count < 5) { //在前4次请求都认为是正常访问
//            valueOperations.increment(key); //调用Redis命令对指定值进行加一操作
//        }else { //在>=5时，说明用户在5秒内请求次数过多，表示在刷接口
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }
        Boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    //自动生成验证码保存到Redis中
    @RequestMapping("/captcha")
    public void happyCaptcha(HttpServletRequest request, HttpServletResponse response, User user, Long goodsId) {
        //HappyCaptcha依赖默认将该验证码保存在session域中，key名称为：happy-captcha
        HappyCaptcha.require(request, response)
                .style(CaptchaStyle.ANIM)            //设置展现样式为动画
                .type(CaptchaType.NUMBER)            //设置验证码内容为数字
                .length(6)                            //设置字符长度为6
                .width(220)                            //设置动画宽度为220
                .height(80)                            //设置动画高度为80
                .font(Fonts.getInstance().zhFont())    //设置汉字的字体
                .build().finish();                //生成并输出验证码
        //将验证码的值保存到Redis中,过期时间为100秒(考虑到分布式，因为分布式项目的session无法共享)
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, (String) request.getSession().getAttribute("happy-captcha"), 100, TimeUnit.SECONDS);
    }

    /*InitializingBean接口的抽象方法，当程序启动时，若该类的属性全部初始化完毕后自动调用该方法(热加载)
     *这里用于完成Redis将DB商品表的商品库存预加载工作，利用Redis原子性减少对DB的并发请求数量
     */
    @Override
    public void afterPropertiesSet() {
        //查询到所有商品的库存量，再将对应商品库存遍历到Redis中(K：seckillGoods:商品Id,V：商品库存)
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) { //判断商品是否为空
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            //在遍历商品库存到Redis时，顺便遍历Map集合(false:表示有库存，true:表示没有库存)
            entryStockMap.put(goodsVo.getId(), false);
        });
    }
}
