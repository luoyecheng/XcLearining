package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    CmsPageService cmsPageService;
    @Autowired
    SySDicRepository sySDicRepository;
    @Test
    public void test()
    {
        String pageHtml = cmsPageService.getPageHtml("5fd6d3c83f86944f08222983");
        System.out.println(pageHtml);
    }
    @Test
    public void testSys()
    {
        SysDictionary sysDictionary = sySDicRepository.findByDType("200");
        System.out.println(sysDictionary);
    }
}
