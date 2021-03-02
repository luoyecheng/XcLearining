package com.xuecheng.rabbitMQ;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer_03_routing {
    //队列 交换机
    private static final String QUEUE_INFORM_EMAIL="queue_inform_email";
    private static final String QUEUE_INFORM_SMS="queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM="exchange_routing_inform";
    private static final String ROUTINGKEY_EMAIL="inform_email";
    private static final String ROUTINGKEY_SMS="inform_sms";
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
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //声明交换机 exchange名称 type交换机类型
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            //交换机和队列绑定 queue队列名称 exchange交换机名称 routingKey路由Key
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_ROUTING_INFORM,ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_ROUTING_INFORM,ROUTINGKEY_SMS);

            for(int i=0;i<5;i++)
            {
                String message="send email inform message";
                //发送消息 exchange交换机 routingKey路由key（默认交换机使用队列名称) props消息属性 body消息内容
                channel.basicPublish(EXCHANGE_ROUTING_INFORM,ROUTINGKEY_EMAIL,null,message.getBytes());
                System.out.println("send to MQ "+message);
            }
            for(int i=0;i<8;i++)
            {
                String message="send sms inform message";
                //发送消息 exchange交换机 routingKey路由key（默认交换机使用队列名称) props消息属性 body消息内容
                channel.basicPublish(EXCHANGE_ROUTING_INFORM,ROUTINGKEY_SMS,null,message.getBytes());
                System.out.println("send to MQ "+message);
            }
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
