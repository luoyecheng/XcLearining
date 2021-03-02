package com.xuecheng.ucenter.test;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test_1 {
    @Autowired
    XcMenuMapper xcMenuMapper;
    @Test
    public void test()
    {
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId("49");
        System.out.println(xcMenus);
    }
}
