package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//查询课程分类
public interface CategoryMapper {
    CategoryNode findCategoryList();
}
