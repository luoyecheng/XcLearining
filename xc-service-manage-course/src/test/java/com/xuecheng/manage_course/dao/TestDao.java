package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseListRequest courseListRequest=new CourseListRequest();
        courseListRequest.setCompanyId("1");
        PageHelper.startPage(1,10);
        Page<CourseInfo> courseList = courseMapper.findCourseList(courseListRequest);
        List<CourseInfo> result = courseList.getResult();
        System.out.println(result);
    }
    @Test
    public void TestCourseInfo()
    {
        Optional<CourseBase> optional=courseBaseRepository.findById("297e7c7c62b888f00162b8a7dec20000");
        if(optional.isPresent())
        {
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }
    }
}
