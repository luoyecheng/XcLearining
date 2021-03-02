package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    CmsPage findByPageName(String pageName);
    CmsPage findByPageNameAndPageType(String pageName,String pageType);
    int countBySiteIdAndPageType(String siteId,String pageType);
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}
