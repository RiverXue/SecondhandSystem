package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.service.OrderService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result createOrder(@RequestParam(name = "goodsId") @NotNull(message = "商品ID不能为空") Long goodsId) {
        System.out.println(goodsId + "正在创建订单");
        return orderService.createOrder(goodsId);
    }

    /**
     * 获取我的订单列表
     */
    @GetMapping("/my")
    public Result getMyOrders() {
        System.out.println(orderService.getMyOrders() + "正在获取我的订单列表");
        return orderService.getMyOrders();
    }

    @GetMapping("/seller")
    public Result getSellerOrders() {
        System.out.println(orderService.getSellerOrders() + "正在获取卖家订单列表");
        return orderService.getSellerOrders();
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    public Result payOrder(@PathVariable @NotNull(message = "订单ID不能为空") Long orderId) {
        return orderService.payOrder(orderId);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result cancelOrder(@PathVariable @NotNull(message = "订单ID不能为空") Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 发货
     */
    @PostMapping("/ship/{orderId}")
    public Result shipOrder(@PathVariable @NotNull(message = "订单ID不能为空") Long orderId) {
        return orderService.shipOrder(orderId);
    }

    /**
     * 完成订单
     */
    @PostMapping("/complete/{orderId}")
    public Result completeOrder(@PathVariable @NotNull(message = "订单ID不能为空") Long orderId) {
        return orderService.completeOrder(orderId);
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{orderId}")
    public Result deleteOrder(@PathVariable @NotNull(message = "订单ID不能为空") Long orderId) {
        return orderService.deleteOrder(orderId);
    }
}