package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Test
    public void testFindAll()
    {
        CmsPage all = cmsPageRepository.findByPageName("index.html");
        System.out.println(all);
    }
    @Test
    public void testFindPage()
    {
        int page=0;
        int size=10;
        Pageable pageable= PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }
    @Test
    public void testFindById()
    {

    }
    @Test
    public void testFindAllByExample()
    {
        int page=0;
        int size=10;
        Pageable pageable=PageRequest.of(page,size);
        CmsPage cmsPage=new CmsPage();
        cmsPage.setPageAliase("轮播");
        ExampleMatcher exampleMatcher=ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }

}
