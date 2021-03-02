package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class pageService {
    private static final Logger LOOGER= LoggerFactory.getLogger(pageService.class);
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;

    //保存html文件到物理路径下
    public void savePageToServePath(String pageId)
    {
        //查询cmspage通过pageid
        CmsPage cmsPage = this.findByPageId(pageId);
        String htmlFileId = cmsPage.getHtmlFileId();
        //查询文件下载流
        InputStream is = this.getFileById(htmlFileId);
        if(is==null)
        {
            LOOGER.error("getFileById inputStream is null ,htmlFileId:",htmlFileId);
            return;
        }
        //得到站点的物理路径
        String siteId = cmsPage.getSiteId();
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //String sitePhysicalPath = "";
        String pagePath=sitePhysicalPath+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream=new FileOutputStream(new File(pagePath));
            IOUtils.copy(is,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {

                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public CmsSite findCmsSiteById(String siteId)
    {
        Optional<CmsSite> optional=cmsSiteRepository.findById(siteId);
        if(optional.isPresent())
        {
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }

    public InputStream getFileById(String fileId)
    {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        GridFSDownloadStream gridFSDownloadStream=gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource=new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //通过pageId查询cmspage
    public CmsPage findByPageId(String pageId)
    {
        Optional<CmsPage> optional=cmsPageRepository.findById(pageId);
        if(optional.isPresent())
        {
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;

    }
}
