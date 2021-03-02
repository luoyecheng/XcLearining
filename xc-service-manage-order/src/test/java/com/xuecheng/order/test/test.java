package com.xuecheng.order.test;

import com.xuecheng.order.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    TaskService taskService;
    @Test
    public void test_1()
    {
        int task = taskService.getTask("1", 1);
        System.out.println(task);
    }
}
