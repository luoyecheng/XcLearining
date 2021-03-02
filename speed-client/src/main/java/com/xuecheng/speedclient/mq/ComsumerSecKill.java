package com.xuecheng.speedclient.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.speed.Order;
import com.xuecheng.speedclient.config.RabbitmqConfig;
import com.xuecheng.speedclient.service.SecKillService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ComsumerSecKill {
    @Autowired
    SecKillService secKillService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String,Order> redisTemplate;
    @RabbitListener(queues = {"queue_routing_speed_seckill"})
    public void postPage(Order order)
    {
//        //解析消息
//        Map map = JSON.parseObject(msg, Map.class);
//        //得到页面Id
//        String pageId = (String) map.get("pageId");
//        //校验页面是否合法
//        CmsPage cmsPage = service.findByPageId(pageId);
//        if(cmsPage==null)
//        {
//            LOGGER.error("receive postpage msg, cmspage is null, pageId:{}",pageId);
//            return;
//        }
//        //调用service方法将页面从GridFS中下载到服务器
//        service.savePageToServePath(pageId);
        //解析消息
        secKillService.insertOrder(order);
    }
}
