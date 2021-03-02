package com.xuecheng.speedclient.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    //交换机名称
    public static final String EX_ROUTING_SPEED_SECKILL="ex_routing_speed_seckill";

    //队列名称
    public static final String QUEUE_ROUTING_SPEED_SECKILL="queue_routing_speed_seckill";

    public static final String QUEUE_ROUTING_SPEED_SECKILL_FINISH="queue_routing_speed_seckill_finish";

    //添加选课路由key
    public static final String SPEED_SECKILL_KEY = "kill";

    public static final String SPEED_SECKILL_FINISH_KEY = "kill-finish";


    @Bean(EX_ROUTING_SPEED_SECKILL)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_SPEED_SECKILL).durable(true).build();
    }

    //声明队列
    @Bean(QUEUE_ROUTING_SPEED_SECKILL)
    public Queue QUEUE_DECLARE() {
        Queue queue = new Queue(QUEUE_ROUTING_SPEED_SECKILL,true,false,true);
        return queue;
    }

    //声明队列
    @Bean(QUEUE_ROUTING_SPEED_SECKILL_FINISH)
    public Queue QUEUE_FINISH_DECLARE() {
        Queue queue = new Queue(QUEUE_ROUTING_SPEED_SECKILL_FINISH,true,false,true);
        return queue;
    }

    /**
     * 绑定队列到交换机 .
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding binding_queue_speed_processtask(@Qualifier(QUEUE_ROUTING_SPEED_SECKILL) Queue queue, @Qualifier(EX_ROUTING_SPEED_SECKILL) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SPEED_SECKILL_KEY).noargs();
    }

    @Bean
    public Binding binding_queue_speed_finish_processtask(@Qualifier(QUEUE_ROUTING_SPEED_SECKILL_FINISH) Queue queue, @Qualifier(EX_ROUTING_SPEED_SECKILL) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SPEED_SECKILL_FINISH_KEY).noargs();
    }
}
