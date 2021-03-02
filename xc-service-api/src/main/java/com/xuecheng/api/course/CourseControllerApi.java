package com.xuecheng.api.course;

import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value ="课程管理接口",description = "课程管理接口 课程增删改查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);
    @ApiOperation("课程计划编辑")
    public ResponseResult addTeachplan(Teachplan teachplan);
    @ApiOperation("课程列表查询")
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);
    @ApiOperation("获取课程基础信息")
    public CourseBase getCourseBaseById(String courseId) throws RuntimeException;
    @ApiOperation("更新课程基础信息")
    public ResponseResult updateCourseBase(String id,CourseBase courseBase);
    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);
    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);

    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId,String pic);
    @ApiOperation("获取课程基础信息")
    public CoursePic findCoursePic(String courseId);
    @ApiOperation("删除课程图片")
    public ResponseResult deleteCoursePic(String courseId);
    @ApiOperation("课程视图查询")
    public CourseView courseView(String id);
    @ApiOperation("课程预览")
    public CoursePublishResult preview(String id);
    @ApiOperation("课程发布")
    public CoursePublishResult publish(String id);
    @ApiOperation("保存课程计划和视频关联")
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia);
}
