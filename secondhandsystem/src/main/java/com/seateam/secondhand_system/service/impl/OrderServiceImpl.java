package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.entity.Order;
import com.seateam.secondhand_system.service.OrderService;
import com.seateam.secondhand_system.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author HK
* @description 针对表【order(订单表)】的数据库操作Service实现
* @createDate 2025-06-17 10:50:27
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




