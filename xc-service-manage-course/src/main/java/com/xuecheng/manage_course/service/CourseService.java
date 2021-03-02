package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.QueryCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class CourseService {
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;

    //课程计划查询
    public TeachplanNode findTeachplanList(String courseid)
    {
        return teachplanMapper.selectList(courseid);
    }
    //添加计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan)
    {
        if(teachplan==null|| StringUtils.isEmpty(teachplan.getCourseid())||StringUtils.isEmpty(teachplan.getPname()))
        {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        if(parentid==null)
        {
            //取出该课程的根节点
            parentid = this.getTeachplanRoot(courseid);
        }
        Optional<Teachplan> optional=teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        String grade = parentNode.getGrade();
        //添加新节点
        Teachplan teachplanNew=new Teachplan();
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        if(grade.equals("1"))
            teachplanNew.setGrade("2");
        else
            teachplanNew.setGrade("3");
        teachplanRepository.save(teachplanNew);
        //处理parentid
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //获取课程计划根节点
    private String getTeachplanRoot(String courseid)
    {
        Optional<CourseBase> optional =courseBaseRepository.findById(courseid);
        if(!optional.isPresent())
            return null;
        CourseBase courseBase = optional.get();
        //查询课程
        List<Teachplan> list = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if(list==null||list.size()<=0)
        {
            //查询不到 自动添加根节点
            Teachplan teachplan=new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseid);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();

        }
        return list.get(0).getId();
    }
    //查询课程列表
    @Transactional
    public QueryResponseResult<CourseInfo> findCourseList(String company_id,int page, int size, CourseListRequest courseListRequest) {
        if(courseListRequest==null)
            courseListRequest=new CourseListRequest();
        //传入公司参数
        courseListRequest.setCompanyId(company_id);
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseList = courseMapper.findCourseList(courseListRequest);
        List<CourseInfo> result = courseList.getResult();
        QueryResult<CourseInfo> queryResult=new QueryResult<>();
        queryResult.setList(result);
        queryResult.setTotal(courseList.getTotal());
        QueryResponseResult<CourseInfo> queryResponseResult=new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
    //通过id查询coursebase
    @Transactional
    public CourseBase findById(String courseid)
    {
        Optional<CourseBase> optional=courseBaseRepository.findById(courseid);
        if(optional.isPresent())
            return optional.get();
        return null;
    }
    //更新coursebase

    //更新coursebase
    @Transactional
    public ResponseResult updateCourseBase(String id,CourseBase courseBase)
    {
        //判断待更新coursebase是否存在
        CourseBase byId = findById(id);
        if(byId==null)
        {
            return new ResponseResult(CommonCode.FAIL);
        }
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据courseid查询课程营销信息
    @Transactional
    public CourseMarket getCourseMarketById(String courseId)
    {
        Optional<CourseMarket> optional=courseMarketRepository.findById(courseId);
        if(!optional.isPresent())
            return null;
        CourseMarket courseMarket = optional.get();
        return courseMarket;
    }

    //更新课程营销信息
    @Transactional
    public ResponseResult updateCourseMarket(String courseid,CourseMarket courseMarket)
    {
        CourseMarket courseMarketById = getCourseMarketById(courseid);
        if(courseMarketById==null)
        {
            CourseMarket save = courseMarketRepository.save(courseMarket);
            if(save==null)
                return new ResponseResult(CommonCode.FAIL);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        CourseMarket save_1 = courseMarketRepository.save(courseMarket);
        if(save_1==null)
            return new ResponseResult(CommonCode.FAIL);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //添加课程图片
    @Transactional
    public ResponseResult saveCoursePic(String courseId,String pic)
    {
        Optional<CoursePic> optional=coursePicRepository.findById(courseId);
        CoursePic coursePic=null;
        if(optional.isPresent())
        {
            coursePic=optional.get();
        }
        if(coursePic==null)
            coursePic=new CoursePic();
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        //保存课程图片
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据id查询coursePic
    public CoursePic findCoursepic(String courseId) {
        Optional<CoursePic> optional=coursePicRepository.findById(courseId);
        if(optional.isPresent())
            return optional.get();
        return null;
    }

    //删除课程图片
    @Transactional
    public  ResponseResult deleteCoursePic(String courseid)
    {
        long result = coursePicRepository.deleteCoursePicsByCourseid(courseid);
        if (result>0)
            return new ResponseResult(CommonCode.SUCCESS);
        return new ResponseResult(CommonCode.FAIL);
    }

    //查询课程视图 基本信息 图片 营销 课程计划
    public CourseView getCourseView(String id)
    {
        CourseView courseView=new CourseView();
        //查询课程基本信息
        Optional<CourseBase> optional=courseBaseRepository.findById(id);
        if(optional.isPresent()) {
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片
//        Optional<CoursePic> optional1=coursePicRepository.findById(id);
//        if(optional1.isPresent())
//        {
//            CoursePic coursePic = optional1.get();
//            courseView.setCoursePic(coursePic);
//        }

        //查询课程营销信息
        Optional<CourseMarket> optional2=courseMarketRepository.findById(id);
        if(optional2.isPresent())
        {
            CourseMarket courseMarket = optional2.get();
            courseView.setCourseMarket(courseMarket);
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }
    //课程预览
    public CoursePublishResult preview(String id) {
        CourseBase courseBase = this.findCourseBaseById(id);
        //请求cms添加页面
        //准备cmspage信息
        CmsPage cmsPage=new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);//页面模板
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBase.getName());//页面别名
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//webpath
        //远程调用
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess())
        {
            System.out.println(1);
        }
        if(!cmsPageResult.isSuccess())
        {
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        String Url=previewUrl+pageId;
        return new CoursePublishResult(CommonCode.SUCCESS,Url);
    }

    private CourseBase findCourseBaseById(String courseId)
    {
        Optional<CourseBase> optional=courseBaseRepository.findById(courseId);
        if(optional.isPresent())
        {
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        return null;
    }

    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //准备页面信息
        CourseBase courseBase = this.findCourseBaseById(id);
        //请求cms添加页面
        CmsPage cmsPage=new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);//页面模板
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBase.getName());//页面别名
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//webpath
        //调用cms一键发布
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPostPageResult.isSuccess())
        {
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程发布状态
        CourseBase courseBase1 = this.saveCourseState(id);
        if(courseBase1==null)
            return new CoursePublishResult(CommonCode.FAIL,null);
        //保存课程索引信息...
        //创建coursepub对象
        CoursePub coursePub = createCoursePub(id);

        saveCoursePub(id,coursePub);
        //缓存课程信息
        //得到页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teachplanmeida保存课程信息
        this.saveTeachplanMedaiPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //向teachplanmeida保存课程信息
    private void saveTeachplanMedaiPub(String courseId)
    {
        //删除teachmeidapub信息
        teachplanMediaPubRepository.deleteTeachplanMediaPubByCourseId(courseId);
        //从teachplanmedia查询记录
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findTeachplanMediaByCourseId(courseId);
        //将list数据插入pub表
        List<TeachplanMediaPub> teachplanMediaPubs=new ArrayList<>();
        for(TeachplanMedia teachplanMedia:teachplanMediaList)
        {
            TeachplanMediaPub teachplanMediaPub=new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }


    //保存coursepub对象
    private CoursePub saveCoursePub(String id,CoursePub coursePub)
    {
        CoursePub coursePubNew=null;
        //根据课程id查询coursepub
        Optional<CoursePub> optional=coursePubRepository.findById(id);
        if(optional.isPresent())
        {
            coursePubNew=optional.get();
        }
        else
        {
            coursePubNew=new CoursePub();
        }
        //保存信息
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
        //发布时间
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    //创建coursepub对象
    private CoursePub createCoursePub(String id)
    {
        CoursePub coursePub=new CoursePub();
        //根据课程id查询
        //课程基本信息
        Optional<CourseBase> optional=courseBaseRepository.findById(id);
        if(optional.isPresent())
        {
            CourseBase courseBase = optional.get();
            //将coursebase属性拷贝到coursepub
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //课程图片信息
        Optional<CoursePic> optional2=coursePicRepository.findById(id);
        if(optional2.isPresent())
        {
            CoursePic coursePic = optional2.get();
            //将coursebase属性拷贝到coursepub
            BeanUtils.copyProperties(coursePic,coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> optional1=courseMarketRepository.findById(id);
        if(optional.isPresent())
        {
            CourseMarket courseMarket = optional1.get();
            //将coursebase属性拷贝到coursepub
            BeanUtils.copyProperties(courseMarket,coursePub);
        }
        //课程计划信息
        //将课程计划信息查询转化json串
        TeachplanNode teachplanNode=teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }


    //更改状态 202002
    private CourseBase saveCourseState(String id)
    {
        CourseBase courseBaseById = this.findCourseBaseById(id);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }
    //保存计划和视频文件关联信息
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        if(teachplanMedia==null||StringUtils.isEmpty(teachplanMedia.getTeachplanId()))
        {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //检查课程计划是否为三级节点
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optional=teachplanRepository.findById(teachplanId);
        if(!optional.isPresent())
        {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        Teachplan teachplan = optional.get();
        String grade = teachplan.getGrade();
        if(StringUtils.isEmpty(grade)||!grade.equals("3"))
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        //查询teachplanmedia
        Optional<TeachplanMedia> optional1=teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one=null;
        if(optional1.isPresent())
            one=optional1.get();
        else
            one=new TeachplanMedia();
        //将one保存到数据库
        one.setCourseId(teachplan.getCourseid());//课程id
        one.setMediaId(teachplanMedia.getMediaId());//视频id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//原始名称
        one.setMediaUrl(teachplanMedia.getMediaUrl());//视频url
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
