package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SySDicRepository;
import com.xuecheng.manage_cms.service.SysDicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysDicServiceImpl implements SysDicService {
    @Autowired
    SySDicRepository sySDicRepository;
    @Override
    public SysDictionary findByType(String type) {
        return sySDicRepository.findByDType(type);
    }
}
