package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SySDicRepository extends MongoRepository<SysDictionary,String> {
    SysDictionary findByDType(String dType);
}
