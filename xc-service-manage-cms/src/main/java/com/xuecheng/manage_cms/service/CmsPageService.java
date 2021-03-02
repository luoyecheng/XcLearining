package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CmsPageService {
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);
    public CmsPageResult add(CmsPage cmsPage);
    public CmsPage findById(String id);
    public CmsPageResult edit(String id,CmsPage cmsPage);
    public ResponseResult deleteById(String id);
    public CmsConfig getConfigById(String id);
    //页面静态化
    public String getPageHtml(String pageId);
    public ResponseResult post(String pageId);
    //页面保存
    public CmsPageResult save(CmsPage cmsPage);
    //一键发布
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
