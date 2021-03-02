package com.xuecheng.speedclient.test;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.speedclient.dao.OrderRepository;
import com.xuecheng.speedclient.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSpeed {
    @Autowired
    OrderService orderService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    OrderRepository orderRepository;

    RestTemplate restTemplate=new RestTemplate();

    private int port=52000;

    private URL base;

    @Before
    public void setUp() throws MalformedURLException {
        String url = String.format("http://localhost:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);

        //测试nginx的正常请求和限流请求
        url_nginx = "http://127.0.0.1:"+port+"/speed/nginx";
        //测试数据库-无锁
        url_nolock = "http://127.0.0.1:"+port+"/speed/seckill";
        //测试乐观锁
        url_optimistic = "http://127.0.0.1:"+port+"/speed/seckillopt";
        //测试带重试的乐观锁
        url_optimisticWithRetry = "http://127.0.0.1:"+port+"/speed/seckilloptRetry";
        //测试悲观锁
        url_pessimistic = "http://127.0.0.1:"+port+"/speed/seckillPes";
        //使用redis原子操作保障原子性
        url_redis = "http://127.0.0.1:"+port+"/speed/seckillRedis";

    }

    //测试nginx的正常请求和限流请求
    String url_nginx;
    //测试数据库-无锁
    String url_nolock;
    //测试乐观锁
    String url_optimistic;
    //测试带重试的乐观锁
    String url_optimisticWithRetry;
    //测试悲观锁
    String url_pessimistic;
    //使用redis原子操作保障原子性
    String url_redis;

    //测试nginx 使用20个并发，测试购买商品使用200个并发
    private static final int amount = 700;
    //发令枪，目的是模拟真正的并发，等所有线程都准备好一起请求
    private CountDownLatch countDownLatch = new CountDownLatch(amount);

    @Test
    public void test()
    {
        System.out.println(url_nginx);
        System.out.println(url_nolock);
        System.out.println(url_optimistic);
        System.out.println(url_pessimistic);
        System.out.println(url_redis);
    }


    @Test
    public void testSpeed() throws InterruptedException {
        System.out.println("开始抢购！当前时间："+(new Date()).toString());
        for (int i = 0; i < amount; i++) {
            new Thread(new Request()).start();
            countDownLatch.countDown();
        }
        Thread.currentThread().sleep(100000);
    }

    @Test
    public void test_redis_reset()
    {
        String key = "seckill" ;
        stringRedisTemplate.boundValueOps(key).set("300",1200, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);
    }

    @Test
    public void test_sec()
    {
        String seckill = stringRedisTemplate.opsForValue().get("seckill");
        System.out.println(seckill);
        //stringRedisTemplate.opsForValue().set("lock","lock");
    }
    @Test
    public void test_redis()
    {
    }

    @Test
    public void test_des()
    {
        stringRedisTemplate.boundValueOps("lock").set("luoyecheng",200,TimeUnit.SECONDS);
        String lock = stringRedisTemplate.opsForValue().get("lock");
        System.out.println(lock);
    }

    @Test
    public void test_del()
    {
        stringRedisTemplate.delete("lock");
        String lock = stringRedisTemplate.opsForValue().get("lock");
        System.out.println(lock);

    }

    @Test
    public void test_redis_service()
    {
        ResponseResult responseResult = orderService.seckillWithRedis();
        System.out.println(responseResult.isSuccess());
    }

    public class Request implements Runnable
    {

        @Override
        public void run() {
            try {
                countDownLatch.await();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            Boolean flag = restTemplate.getForObject(url_nginx, Boolean.class);
            if(flag!=null&&flag)
            {
                Boolean sale_flag = restTemplate.getForObject(url_redis, Boolean.class);
                if (sale_flag!=null&&sale_flag)
                    System.out.println("抢到了！当前时间："+(new Date()).toString());
                else
                    System.out.println("没抢到！当前时间:"+(new Date()).toString());
                //System.out.println("success!");
            }
            else
                System.out.println("人太多了，没挤进去！");

        }
    }


}
