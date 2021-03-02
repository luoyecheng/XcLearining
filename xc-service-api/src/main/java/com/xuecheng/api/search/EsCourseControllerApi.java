package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Api(value ="课程管理接口",description = "课程管理接口 课程增删改查")
public interface EsCourseControllerApi {
    //搜索课程信息
    @ApiOperation("课程搜索")
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam);
    @ApiOperation("根据课程id查询课程信息")
    public Map<String,CoursePub> getAll(String id);
    @ApiOperation("根据课程计划id查询课程媒体资料信息")
    public TeachplanMediaPub getMedia(String teachplanId);
}
