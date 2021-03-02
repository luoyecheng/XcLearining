package com.xuecheng.speedclient.mq;

import com.xuecheng.framework.domain.speed.Order;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.speedclient.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SeckillMQ {
    @Autowired
    private RedisTemplate<String, Order> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //每隔1分钟扫描消息表，向mq发送消息
    //@Scheduled(fixedDelay = 60000)
    public void sendChoosecourseTask()
    {
        System.out.println("=========扫描========");
        Set<Order> orders = redisTemplate.opsForSet().difference("SECKILL_SET", "SECKILL_SET_CHECK");
        if(orders.size()==0)
            return;
        for(Order order:orders)
        {
            rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_SPEED_SECKILL,RabbitmqConfig.SPEED_SECKILL_KEY,order);
        }
    }


    //@RabbitListener(queues = {RabbitmqConfig.QUEUE_ROUTING_SPEED_SECKILL_FINISH})
    public void seckill_finish(Order order)
    {
        redisTemplate.opsForSet().remove("SECKILL_SET",order);
    }
}
