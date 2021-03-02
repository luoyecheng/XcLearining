package com.xuecheng.rabbitMQ;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//入门程序
public class Producer_01 {
    //队列
    private static final String QUEUE="helloworld";
    public static void main(String[] args) {
        //通过工厂创建新的连接 和mq建立连接
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        //建立新连接
        Connection connection=null;
        Channel channel = null;
        try {
            connection=connectionFactory.newConnection();
            //创建会话通道
            channel = connection.createChannel();
            //声明队列 名字  是否持久化 是否排他性(true=>临时队列) 是否自动删除 拓展参数
            channel.queueDeclare(QUEUE, true, false, false, null);
            String message="hello zhuzhu";
            //发送消息 exchange交换机 routingKey路由key（默认交换机使用队列名称) props消息属性 body消息内容
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to MQ "+message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        finally {
            //关闭连接
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
