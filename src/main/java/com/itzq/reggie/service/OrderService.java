package com.itzq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzq.reggie.entity.Orders;
import org.springframework.stereotype.Service;

@Service
public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
