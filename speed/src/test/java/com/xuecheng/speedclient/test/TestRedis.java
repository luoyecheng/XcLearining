package com.xuecheng.speedclient.test;

import com.xuecheng.framework.domain.speed.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Test
    public void test_ops()
    {
        Long add = redisTemplate.opsForSet().add("111", "1");
        System.out.println(add);

    }
    @Test
    public void test_ops_2()
    {
        Set<Object> difference = redisTemplate.opsForSet().difference("orders", "orders_1");
        for(Object order:difference)
        {
            Order order_1= (Order) order;
            redisTemplate.opsForSet().remove("orders",order_1);

        }
    }
}
