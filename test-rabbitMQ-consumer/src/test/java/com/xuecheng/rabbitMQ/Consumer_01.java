package com.xuecheng.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//入门程序
public class Consumer_01 {
    //队列
    private static final String QUEUE="helloworld";
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
        channel.queueDeclare(QUEUE, true, false, false, null);
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
        channel.basicConsume(QUEUE,true,defaultConsumer);
    }

}
