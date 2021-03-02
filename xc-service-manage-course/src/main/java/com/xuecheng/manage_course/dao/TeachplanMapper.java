package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.QueryCourseResult;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TeachplanMapper {
   //课程计划查询
   public TeachplanNode selectList(String courseId);
}
