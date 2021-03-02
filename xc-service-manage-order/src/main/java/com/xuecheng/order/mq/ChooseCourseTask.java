package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {
    private static final Logger LOGGER= LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    //定时发送选课任务
    //@Scheduled(cron = "0/5 * * * * *")
    @Scheduled(fixedDelay = 60000)
    public void sendChooseCourseTask()
    {
        //得到一分钟之前的时间
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        System.out.println(xcTaskList);
        //调用service发布消息 将选课任务发送给mq
        for (XcTask xcTask : xcTaskList) {
            //获取任务
            if(taskService.getTask(xcTask.getId(),xcTask.getVersion())>0)
            {
                String mqExchange = xcTask.getMqExchange();//交换机
                String mqRoutingkey = xcTask.getMqRoutingkey();//routingkey
                taskService.publish(xcTask,mqExchange,mqRoutingkey);
                LOGGER.info("发送消息");
            }
        }
    }

    //每隔1分钟扫描消息表，向mq发送消息
    //@Scheduled(fixedDelay = 60000)
    public void sendChoosecourseTask()
    {
        //取出当前时间1分钟之前的时间
        Calendar calendar =new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findXcTaskList(time, 1000);
        System.out.println("task2:"+taskList.size());
    }


    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChooseCourseTask(XcTask xcTask)
    {
        if(xcTask!=null&& StringUtils.isNotEmpty(xcTask.getId()))
        {
            taskService.finishTask(xcTask.getId());
            LOGGER.info("流程顺利完成");
        }
    }










    //@Scheduled(fixedRate = 3000)
    public void task1(){
        LOGGER.info("==================测试定时任务1开始================");
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        LOGGER.info("==================测试定时任务1结束================");

    }

    //@Scheduled(fixedRate = 3000)
    public void task2(){
        LOGGER.info("==================测试定时任务2开始================");
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        LOGGER.info("==================测试定时任务2结束================");

    }
}
