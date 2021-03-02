package com.xuecheng.speedclient.dao;

import com.xuecheng.framework.domain.speed.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
