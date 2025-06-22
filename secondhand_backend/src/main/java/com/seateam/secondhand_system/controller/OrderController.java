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
}