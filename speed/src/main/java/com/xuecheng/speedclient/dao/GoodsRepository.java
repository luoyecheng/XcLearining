package com.xuecheng.speedclient.dao;

import com.xuecheng.framework.domain.speed.Goods;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface GoodsRepository extends JpaRepository<Goods,Integer> {
    @Transactional
    @Modifying
    @Query("update Goods g set g.name=:name,g.count=:count,g.sale=:sale,g.version=:version where g.id=:id")
    int updateGoodsCount(@Param(value="name") String name,@Param(value="count")int count,@Param(value="sale")int sale,@Param(value="version")int version,@Param(value="id")int id);

    @Transactional
    @Modifying
    @Query("update Goods g set g.name = :name, g.count = :count, g.sale = :sale, g.version = :version+1 WHERE g.id = :id and g.version = :version")
    int updateGoodsCountOptimisticLock(@Param(value="name") String name,@Param(value="count")int count,@Param(value="sale")int sale,@Param(value="version")int version,@Param(value="id")int id);



}
