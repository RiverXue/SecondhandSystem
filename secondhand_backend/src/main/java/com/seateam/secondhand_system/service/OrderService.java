package com.seateam.secondhand_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.Order;

/**
 * @author HK
 * @description 针对表【order(订单表)】的数据库操作Service
 * @createDate 2025-06-17 10:50:27
 */
public interface OrderService extends IService<Order> {
    /**
     * 创建订单
     *
     * @param goodsId 商品ID
     * @return 操作结果
     */
    Result createOrder(Long goodsId);

    /**
     * 获取我的订单列表
     *
     * @return 订单列表
     */
    Result getMyOrders();
}
