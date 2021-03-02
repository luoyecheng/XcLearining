package com.xuecheng.speedclient.test;

import com.xuecheng.framework.domain.speed.Goods;
import com.xuecheng.framework.domain.speed.Order;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.speedclient.dao.GoodsRepository;
import com.xuecheng.speedclient.dao.OrderRepository;
import com.xuecheng.speedclient.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJpa {
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderService orderService;
    @Test
    public void test_1()
    {
        List<Goods> all = goodsRepository.findAll();
        System.out.println(all);
        int count = goodsRepository.updateGoodsCountOptimisticLock("zhuzhu", 103, 10, 1, 1);
        System.out.println(count);
    }

    @Test
    public void test_2()
    {
        List<Order> all = orderRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void test_3()
    {
        Optional<Goods> optional=goodsRepository.findById(1);
        System.out.println(optional.orElse(null));
    }

    @Test
    public void test_4()
    {
        ResponseResult responseResult = orderService.seckillWithRedis();
        System.out.println(responseResult.isSuccess());
    }

    @Test
    public void test_5()
    {
        orderService.insertOrder("luobo",new Date());
    }
}
