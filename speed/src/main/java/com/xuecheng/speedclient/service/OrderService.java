package com.xuecheng.speedclient.service;

import com.xuecheng.framework.domain.speed.Goods;
import com.xuecheng.framework.domain.speed.Order;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.speedclient.dao.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    //交换机名称
    public static final String EX_ROUTING_SPEED_SECKILL="ex_routing_speed_seckill";

    //添加选课路由key
    public static final String SPEED_SECKILL_KEY = "kill";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;
    //插入订单
    @Transactional
    public int insertOrder(String name,Date createTime)
    {
        Order order=new Order();
        order.setCustname(name);
        order.setSpeed_create_time(createTime);
        Order save = orderRepository.save(order);
        if(save.getId()==null)
                return 0;
        return 1;
    }

    @Transactional
    public ResponseResult seckillPessimism() throws SQLException {
        //悲观锁
//        SqlSession sqlSession=sqlSessionFactory.openSession(false);
//        sqlSession.getConnection().setAutoCommit(false);

        //查询库存
        int id=1;//静态id
        Goods goods = goodsService.getGoods(id);
        if(goods==null)
        {
            System.out.println("商品不存在！");
            return new ResponseResult(CommonCode.FAIL);
        }
        if(goods.getCount()<=0)
        {
            System.out.println(Thread.currentThread().getName()+"悲观锁方式商品卖完了!当前时间："+System.currentTimeMillis());
            return new ResponseResult(CommonCode.FAIL);
        }
        Goods goodsForUpdate=new Goods();
        BeanUtils.copyProperties(goods,goodsForUpdate);
        goodsForUpdate.setCount(goods.getCount()-1);
        goodsForUpdate.setSale(goods.getSale()+1);
        int i = goodsService.updateGoodsCount(goodsForUpdate);
        //当库存更新成功后创建订单
        String username="zhuzhu";//静态用户名
        if(i>0)
        {
            //创建订单
            Date createTime=new Date();
            String timeStr = createTime.toString();
            String custname=username+timeStr;
            insertOrder(custname,createTime);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
//        sqlSession.getConnection().commit();
    }

    //乐观锁
    @Transactional
    public ResponseResult seckillOptimistic()
    {
        //查询库存 满足条件则执行秒杀逻辑
        int id=1;//静态id
        Goods goods = goodsService.getGoods(id);
        if(goods==null)
        {
            System.out.println("商品不存在!");
            return new ResponseResult(CommonCode.FAIL);
        }
        if(goods.getCount()<=0)
        {
            System.out.println(Thread.currentThread().getName()+"商品以乐观锁方式卖完了!当前时间："+System.currentTimeMillis());
            return new ResponseResult(CommonCode.FAIL);
        }
        //int currentVersion=goods.getVersion();
        Goods goodsForUpdate=new Goods();
        BeanUtils.copyProperties(goods,goodsForUpdate);
        goodsForUpdate.setCount(goods.getCount()-1);
        goodsForUpdate.setSale(goods.getSale()+1);
        int i = goodsService.updateGoodsCountOptimisticLock(goodsForUpdate);

        //当库存更新成功后创建订单
        String username="zhuzhu";//静态用户名
        if(i>0)
        {
            Date createTime=new Date();
            String timeStr = createTime.toString();
            String custname=username+timeStr;
            insertOrder(custname,createTime);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //会重试的乐观锁
    @Transactional
    public int seckillWithOptimistic()
    {
        int id=1;//静态id
        //查询库存 满足条件 继续秒杀逻辑
        Goods goods = goodsService.getGoods(id);
        if(goods==null)
        {
            System.out.println("商品不存在!");
            return -1;
        }
        if(goods.getCount()<=0)
        {
            System.out.println("商品以乐观锁方式卖完了！当前时间："+System.currentTimeMillis());
            return -1;
        }
        Goods goodsForUpdate=new Goods();
        BeanUtils.copyProperties(goods,goodsForUpdate);
        goodsForUpdate.setCount(goods.getCount()-1);
        goodsForUpdate.setSale(goods.getSale()+1);
        int i = goodsService.updateGoodsCountOptimisticLock(goodsForUpdate);

        //当库存更新成功后创建订单
        String username="zhuzhu";//静态用户名
        if(i>0)
        {
            Date createTime=new Date();
            String timeStr = createTime.toString();
            String custname=username+timeStr;
            insertOrder(custname,createTime);
            return 1;
        }else
        {
            //重试
            return 0;
        }
    }

    //无锁
    @Transactional
    public ResponseResult seckill()
    {
        int id=1;//静态id
        //查询库存 满足条件 继续秒杀逻辑
        Goods goods = goodsService.getGoods(id);
        if(goods==null)
        {
            System.out.println("商品不存在!");
            return new ResponseResult(CommonCode.FAIL);
        }
        if(goods.getCount()<=0)
        {
            System.out.println("商品以乐观锁方式卖完了！当前时间："+System.currentTimeMillis());
            return new ResponseResult(CommonCode.FAIL);
        }
        Goods goodsForUpdate=new Goods();
        BeanUtils.copyProperties(goods,goodsForUpdate);
        goodsForUpdate.setCount(goods.getCount()-1);
        goodsForUpdate.setSale(goods.getSale()+1);
        int i = goodsService.updateGoodsCount(goodsForUpdate);

        //当库存更新成功后创建订单
        String username="zhuzhu";//静态用户名
        if(i>0)
        {
            Date createTime=new Date();
            String timeStr = createTime.toString();
            String custname=username+timeStr;
            insertOrder(custname,createTime);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //使用redis原子操作保持原子性
    public ResponseResult seckillWithRedis()
    {
//        String key="seckill";//定义key key即商品数量
//        int num=Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key)));
//        if(num<=0)
//            return new ResponseResult(CommonCode.FAIL);
//        stringRedisTemplate.setEnableTransactionSupport(true);
//        stringRedisTemplate.watch(key);
//        stringRedisTemplate.multi();
//        stringRedisTemplate.opsForValue().increment(key,-1);
//        List<Object> result = stringRedisTemplate.exec();
//        if(result.size()>0)
//        {
//            String username="zhuzhu";//静态用户名
//            Date create_time=new Date();
//            String custname=username+create_time.toString();
//            Order order=new Order();
//            order.setCustname(custname);
//            order.setSpeed_create_time(create_time);
//            rabbitTemplate.convertAndSend(EX_ROUTING_SPEED_SECKILL,SPEED_SECKILL_KEY,order);
//            //insertOrder(custname,create_time);
//            return new ResponseResult(CommonCode.SUCCESS);
//        }
//        return new ResponseResult(CommonCode.FAIL);
        String key="seckill";
        String key_lock="lock";
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(key_lock, "lock");
        if(!lock)
            return new ResponseResult(CommonCode.FAIL);
        if(Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key)))<=0)
        {
            stringRedisTemplate.delete(key_lock);
            return new ResponseResult(CommonCode.FAIL);
        }
        stringRedisTemplate.opsForValue().increment(key,-1);
        stringRedisTemplate.delete(key_lock);
        String username="zhuzhu";//静态用户名
        Date create_time=new Date();
        String custname=username+create_time.toString();
        Order order=new Order();
        order.setSpeed_create_time(create_time);
        order.setCustname(custname);
        //redisTemplate.opsForSet().add("SECKILL_SET",order);
        rabbitTemplate.convertAndSend(EX_ROUTING_SPEED_SECKILL,SPEED_SECKILL_KEY,order);
        return new ResponseResult(CommonCode.SUCCESS);
    }

}
