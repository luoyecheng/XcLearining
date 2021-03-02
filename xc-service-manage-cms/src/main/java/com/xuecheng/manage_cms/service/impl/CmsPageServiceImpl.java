package com.xuecheng.manage_cms.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.CmsPageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.util.internal.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CmsPageServiceImpl implements CmsPageService {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsConfigRepository cmsConfigRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    CmsSiteRepository cmsSiteRepository;
    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if(queryPageRequest==null)
            queryPageRequest=new QueryPageRequest();
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage=new CmsPage();
        if(!StringUtil.isNullOrEmpty(queryPageRequest.getSiteId()))
        {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if(!StringUtil.isNullOrEmpty(queryPageRequest.getTemplateId()))
        {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        if(!StringUtil.isNullOrEmpty(queryPageRequest.getPageAliase()))
        {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);

        if(page<=0)
            page=1;
        page=page-1;
        if(size<=0)
            size=10;
        Pageable pageable= PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult queryResult=new QueryResult();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    @Override
    //新增页面
    public CmsPageResult add(CmsPage cmsPage) {
        if(cmsPage==null)
        {
            //异常
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage cmspage_1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmspage_1!=null)
        {
            //异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }

    @Override
    //根据id查询
    public CmsPage findById(String id) {
        Optional<CmsPage> optional =cmsPageRepository.findById(id);
        if(optional.isPresent())
        {
            CmsPage cmsPage=optional.get();
            return cmsPage;
        }
        return  null;
    }

    @Override
    //编辑页面
    public CmsPageResult edit(String id, CmsPage cmsPage) {
        CmsPage cmsPage_find = findById(id);
        if(cmsPage_find!=null)
        {
            cmsPage_find.setTemplateId(cmsPage.getTemplateId());
            cmsPage_find.setSiteId(cmsPage.getSiteId());
            cmsPage_find.setPageAliase(cmsPage.getPageAliase());
            cmsPage_find.setPageName(cmsPage.getPageName());
            cmsPage_find.setPageWebPath(cmsPage.getPageWebPath());
            cmsPage_find.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            cmsPage_find.setDataUrl(cmsPage.getDataUrl());
            cmsPageRepository.save(cmsPage_find);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage_find);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    @Override
    //根据Id删除
    public ResponseResult deleteById(String id) {
        Optional<CmsPage> optional=cmsPageRepository.findById(id);
        if(optional.isPresent())
        {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    @Override
    //根据id查询config
    public CmsConfig getConfigById(String id) {
        Optional<CmsConfig> optional=cmsConfigRepository.findById(id);
        if(optional.isPresent())
        {
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return  null;
    }

    @Override
    //获取静态化页面
    public String getPageHtml(String pageId) {
        Map model= getModelByPageID(pageId);
        if(model==null)
        {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        String template = getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(template))
        {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        String html = generateHtml(template, model);
        return html;
    }

    @Override
    //页面发布
    public ResponseResult post(String pageId) {
        //执行页面静态化
        String pageHtml=this.getPageHtml(pageId);
        //保存文件到Gridfs
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //保存页面 有则更新 没有则添加
    @Override
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(one!=null)
        {
            return this.edit(one.getPageId(),cmsPage);
        }
        return this.add(cmsPage);
    }

    //一键发布
    @Override
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //存储页面信息到cms_page
        CmsPageResult save = this.save(cmsPage);
        if(!save.isSuccess())
        {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage one = save.getCmsPage();
        //得到页面id
        String pageId = one.getPageId();
        //页面发布
        ResponseResult post = this.post(pageId);
        if(!post.isSuccess())
        {
            ExceptionCast.cast(CommonCode.FAIL);
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        //拼接页面url
        String siteId = one.getSiteId();
        CmsSite cmsSiteById = this.findCmsSiteById(siteId);
        String pageUrl=cmsSiteById.getSiteDomain()+cmsSiteById.getSiteWebPath()+one.getPageWebPath()+one.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    private CmsSite findCmsSiteById(String siteId)
    {
        Optional<CmsSite> optional=cmsSiteRepository.findById(siteId);
        if(optional.isPresent())
            return optional.get();
        return null;
    }

    //向mq发送消息
    private void sendPostPage(String pageId)
    {
        //得到页面信息
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage==null)
        {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //创建消息体
        Map<String,String> msg=new HashMap<>();
        msg.put("pageId",pageId);
        String json = JSON.toJSONString(msg);
        String siteId = cmsPage.getSiteId();
        //发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,json);
    }
    //保存html
    private CmsPage saveHtml(String pageId,String htmlContent)
    {
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage==null)
        {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        ObjectId objectId=null;
        InputStream inputStream=null;
        try {
            inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //保存html到gridfs
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    private String generateHtml(String templateContent,Map model)
    {
        Configuration configuration=new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader=new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        configuration.setTemplateLoader(stringTemplateLoader);
        try {
            Template template = configuration.getTemplate("template");
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTemplateByPageId(String pageId)
    {
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage==null)
        {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId))
        {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional= cmsTemplateRepository.findById(templateId);
        if(optional.isPresent())
        {
            CmsTemplate cmsTemplate = optional.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            GridFsResource gridFsResource=new GridFsResource(gridFSFile,gridFSDownloadStream);
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private Map getModelByPageID(String pageId)
    {
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage==null)
        {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXIST);
        }
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl))
        {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }
}
