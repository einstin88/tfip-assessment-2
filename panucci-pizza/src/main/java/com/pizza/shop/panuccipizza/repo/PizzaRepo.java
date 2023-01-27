package com.pizza.shop.panuccipizza.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.pizza.shop.panuccipizza.model.Order;

@Repository
public class PizzaRepo {
    @Autowired
    RedisTemplate<String, Object> template;

    public void saveOrder(Order order) {
        template.opsForValue().set(order.getOrderId(), order);
    }
}
