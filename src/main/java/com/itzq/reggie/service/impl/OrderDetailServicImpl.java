package com.itzq.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzq.reggie.entity.OrderDetail;
import com.itzq.reggie.mapper.OrderDetailMapper;
import com.itzq.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServicImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>implements OrderDetailService {
}
