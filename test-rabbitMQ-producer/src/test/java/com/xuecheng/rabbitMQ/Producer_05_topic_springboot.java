package com.xuecheng.rabbitMQ;

import com.xuecheng.test.rabbitMQ.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer_05_topic_springboot {
//    @Autowired
//    RabbitTemplate rabbitTemplate;
    @Test
    public void test_01()
    {
        String message="send email to user";
        //rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPIC_INFORM,"inform.email",message);
    }
}
