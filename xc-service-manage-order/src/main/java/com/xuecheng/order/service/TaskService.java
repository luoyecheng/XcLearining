package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHistoryRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHistoryRepository xcTaskHistoryRepository;
    //查询前n条记录
    public List<XcTask> findXcTaskList(Date updateTime,int size)
    {
        //设置分页参数
        Pageable pageable=new PageRequest(0,size);
        //查询记录
        Page<XcTask> all = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        List<XcTask> list = all.getContent();
        return list;
    }

    //发送消息
    public void publish(XcTask xcTask,String ex,String routingKey)
    {
        Optional<XcTask> optional =xcTaskRepository.findById(xcTask.getId());
        if(optional.isPresent())
        {
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务时间
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    //获取任务
    @Transactional
    public int getTask(String id,int version)
    {
        //乐观锁更新数据表
        int count = xcTaskRepository.updateTaskVersion(id, version);
        return count;
    }

    //完成任务
    @Transactional
    public void finishTask(String taskId)
    {
        Optional<XcTask> optional=xcTaskRepository.findById(taskId);
        if(optional.isPresent())
        {
            //当前任务
            XcTask xcTask = optional.get();
            XcTaskHis xcTaskHis=new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHistoryRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }

}