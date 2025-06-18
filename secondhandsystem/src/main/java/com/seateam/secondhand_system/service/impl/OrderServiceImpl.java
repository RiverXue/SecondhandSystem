package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.entity.Order;
import com.seateam.secondhand_system.service.OrderService;
import com.seateam.secondhand_system.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.ResultCode;
import com.seateam.secondhand_system.common.utils.JwtUtils;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
* @author HK
* @description 针对表【order(订单表)】的数据库操作Service实现
* @createDate 2025-06-17 10:50:27
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 创建订单
     */
    @Override
    @Transactional
    public Result createOrder(Long goodsId) {
        // 验证参数
        if (goodsId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "商品ID不能为空");
        }

        // 获取当前用户ID（买家）
        Long buyerId = jwtUtils.getCurrentUserId();
        if (buyerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 查询商品信息
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.error(ResultCode.NOT_FOUND, "商品不存在");
        }

        // 验证商品状态（0-在售）
        if (goods.getStatus() != 0) {
            return Result.error(ResultCode.ERROR, "商品已售出");
        }

        // 验证买家不能是卖家
        if (goods.getUserId().equals(buyerId)) {
            return Result.error(ResultCode.ERROR, "不能购买自己发布的商品");
        }

        // 创建订单
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getUserId());
        order.setGoodsId(goodsId);
        order.setPrice(goods.getPrice());
        order.setStatus(0); // 0-未付款

        // 保存订单
        int rows = orderMapper.insert(order);
        if (rows <= 0) {
            return Result.error("订单创建失败");
        }

        // 更新商品状态为已售
        goods.setStatus(1);
        goodsMapper.updateById(goods);

        return Result.success("订单创建成功").put("orderId", order.getId());
    }

    /**
     * 获取我的订单列表
     */
    @Override
    public Result getMyOrders() {
        // 获取当前用户ID（买家）
        Long buyerId = jwtUtils.getCurrentUserId();
        if (buyerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 查询订单列表
        List<Order> orderList = orderMapper.selectByBuyerId(buyerId);
        return Result.success().put("orderList", orderList);
    }
}




