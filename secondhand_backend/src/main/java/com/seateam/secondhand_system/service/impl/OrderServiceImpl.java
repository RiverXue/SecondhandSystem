package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.ResultCode;
import com.seateam.secondhand_system.common.utils.JwtUtils;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.entity.Order;
import com.seateam.secondhand_system.mapper.GoodsMapper;
import com.seateam.secondhand_system.mapper.OrderMapper;
import com.seateam.secondhand_system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HK
 * @description 针对表【order(订单表)】的数据库操作Service实现
 * @createDate 2025-06-17 10:50:27
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

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
        int rows = orderMapper.insertOrder(order);
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

    @Override
    public Result getSellerOrders() {
        // 获取当前用户ID（卖家）
        Long sellerId = jwtUtils.getCurrentUserId();
        if (sellerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 查询订单列表
        List<Order> orderList = orderMapper.selectBySellerId(sellerId);
        return Result.success().put("orderList", orderList);
    }


    /**
     * 支付订单
     */
    @Override
    @Transactional
    public Result payOrder(Long orderId) {
        // 验证参数
        if (orderId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "订单ID不能为空");
        }

        // 获取当前用户ID（买家）
        Long buyerId = jwtUtils.getCurrentUserId();
        if (buyerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 更新订单状态为已付款
        int rows = orderMapper.updateOrderStatus(orderId, 1, buyerId, false);
        if (rows <= 0) {
            return Result.error("订单支付失败或订单状态错误");
        }

        return Result.success("订单支付成功");
    }


    /**
     * 取消订单
     */
    @Override
    @Transactional
    public Result cancelOrder(Long orderId) {
        // 验证参数
        if (orderId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "订单ID不能为空");
        }

        // 获取当前用户ID（买家）
        Long buyerId = jwtUtils.getCurrentUserId();
        if (buyerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 更新订单状态为已取消
        int rows = orderMapper.updateOrderStatus(orderId, 4, buyerId, false);
        if (rows <= 0) {
            return Result.error("订单取消失败或订单状态错误");
        }

        // 查询订单信息
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            // 将商品状态改回在售
            Goods goods = goodsMapper.selectById(order.getGoodsId());
            if (goods != null) {
                goods.setStatus(0);
                goodsMapper.updateById(goods);
            }
        }

        return Result.success("订单取消成功");
    }


    /**
     * 发货订单
     */
    @Override
    @Transactional
    public Result shipOrder(Long orderId) {
        // 验证参数
        if (orderId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "订单ID不能为空");
        }

        // 获取当前用户ID（卖家）
        Long sellerId = jwtUtils.getCurrentUserId();
        if (sellerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 查询订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(ResultCode.NOT_FOUND, "订单不存在");
        }

        // 验证卖家身份
        if (!order.getSellerId().equals(sellerId)) {
            return Result.error(ResultCode.FORBIDDEN, "无权操作此订单");
        }

        // 验证订单状态
        if (order.getStatus() != 1) {
            return Result.error(ResultCode.ERROR, "只有已付款订单可以发货");
        }

        // 更新订单状态为已发货（使用卖家ID作为条件）
        int rows = orderMapper.updateOrderStatus(orderId, 2, sellerId, true);
        if (rows <= 0) {
            return Result.error("订单发货失败");
        }

        return Result.success("订单发货成功");
    }


    /**
     * 完成订单
     */
    @Override
    @Transactional
    public Result completeOrder(Long orderId) {
        // 验证参数
        if (orderId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "订单ID不能为空");
        }

        // 获取当前用户ID（买家）
        Long buyerId = jwtUtils.getCurrentUserId();
        if (buyerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 查询订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(ResultCode.NOT_FOUND, "订单不存在");
        }

        // 验证买家身份
        if (!order.getBuyerId().equals(buyerId)) {
            return Result.error(ResultCode.FORBIDDEN, "无权操作此订单");
        }

        // 验证订单状态
        if (order.getStatus() != 2) {
            return Result.error(ResultCode.ERROR, "只有已发货订单可以确认完成");
        }

        // 更新订单状态为已完成
        int rows = orderMapper.updateOrderStatus(orderId, 3, buyerId, false);
        if (rows <= 0) {
            return Result.error("订单完成失败");
        }

        return Result.success("订单完成成功");
    }

    /**
     * 删除订单
     */
    @Override
    @Transactional
    public Result deleteOrder(Long orderId) {
        // 验证参数
        if (orderId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "订单ID不能为空");
        }

        // 获取当前用户ID（买家）
        Long buyerId = jwtUtils.getCurrentUserId();
        if (buyerId == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "请先登录");
        }

        // 查询订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(ResultCode.NOT_FOUND, "订单不存在");
        }

        // 验证买家身份
        if (!order.getBuyerId().equals(buyerId)) {
            return Result.error(ResultCode.FORBIDDEN, "无权操作此订单");
        }

        // 验证订单状态（只有已取消订单可以删除）
        if (order.getStatus() != 4) {
            return Result.error(ResultCode.ERROR, "只有已取消订单可以删除");
        }

        // 删除订单
        int rows = orderMapper.deleteById(orderId);
        if (rows <= 0) {
            return Result.error("订单删除失败");
        }

        return Result.success("订单删除成功");
    }
}




