package com.seateam.secondhand_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seateam.secondhand_system.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HK
 * @description 针对表【order(订单表)】的数据库操作Mapper
 * @createDate 2025-06-17 10:50:27
 * @Entity com.seateam.secondhand_system.entity.Order
 */
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> selectBySellerId(Long sellerId);

    List<Order> selectByBuyerId(@Param("buyerId") Long buyerId);

    /**
     * 创建订单
     * @param order 订单信息
     * @return 影响行数
     */
    int insertOrder(Order order);

    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status, @Param("userId") Long userId, @Param("isSeller") boolean isSeller);
}




