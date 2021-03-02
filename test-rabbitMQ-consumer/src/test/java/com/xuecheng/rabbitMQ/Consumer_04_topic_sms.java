package com.xuecheng.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer_04_topic_sms {
    //队列 交换机
    private static final String QUEUE_INFORM_SMS="queue_inform_sms";
    private static final String EXCHANGE_TOPIC_INFORM="exchange_TOPIC_inform";
    private static final String ROUTINGKEY_SMS="inform.#.sms.#";
    public static void main(String[] args) throws IOException, TimeoutException {
        //通过工厂创建新的连接 和mq建立连接
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        //建立新连接
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列 名字  是否持久化 是否排他性(true=>临时队列) 是否自动删除 拓展参数
        channel.exchangeDeclare(EXCHANGE_TOPIC_INFORM,BuiltinExchangeType.TOPIC);
        channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
        //绑定交换机
        channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_TOPIC_INFORM,ROUTINGKEY_SMS);
        //实现消费方法
        DefaultConsumer defaultConsumer=new DefaultConsumer(channel){
            //接收消息时调用 consumerTag消费者标签（监听队列设置） envelope信封（拿到交换机) properties消息属性 body消息内容
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //消息Id 标识消息Id 确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                String message=new String(body,"utf-8");
                System.out.println("receive message:"+message);
            }
        };
        //监听队列 queue队列名称 autoAsk自动回复（自动回复消息已接收） callback消费方法
        channel.basicConsume(QUEUE_INFORM_SMS,true,defaultConsumer);
    }

}
