package com.xuecheng.speedclient.service;

import com.xuecheng.framework.domain.speed.Goods;
import com.xuecheng.speedclient.dao.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class GoodsService {
    @Autowired
    private GoodsRepository goodsRepository;

    //更新货品数量
    public int updateGoodsCount(Goods goods)
    {
        return goodsRepository.updateGoodsCount(goods.getName(),goods.getCount(),goods.getSale(),goods.getVersion(),goods.getId());
    }

    public int updateGoodsCountOptimisticLock(Goods goods)
    {
        return goodsRepository.updateGoodsCountOptimisticLock(goods.getName(),goods.getCount(),goods.getSale(),goods.getVersion(),goods.getId());
    }

    public Goods getGoods(int id)
    {
        Optional<Goods> optional =goodsRepository.findById(id);
        return optional.orElse(null);
    }
}
