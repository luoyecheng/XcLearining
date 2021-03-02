package com.xuecheng.speedclient.service;

import com.xuecheng.framework.domain.speed.Order;
import com.xuecheng.speedclient.dao.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecKillService {
    @Autowired
    OrderRepository orderRepository;
    public void insertOrder(Order order)
    {
        orderRepository.save(order);
    }
}
