package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 发布商品
     */
    @PostMapping("/publish")
    public Result publishGoods(@RequestBody Goods goods) {
        return goodsService.publishGoods(goods);
    }

    /**
     * 商品分页列表
     * 用于前台展示商品列表
     */
    @GetMapping("/list")
    public Result getGoodsPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return goodsService.getGoodsPage(pageNum, pageSize);
    }

    /**
     * 获取商品详情
     * 用于前台展示商品详情
     */
    @GetMapping("/detail/{id}")
    public Result getGoodsDetail(@PathVariable(name = "id") Long id) {
        System.out.println("前端传来的id：" + id + "，正在获取商品详情");
        return goodsService.getGoodsDetail(id);
    }

    /**
     * 搜索商品
     * 用于前台搜索商品
     */
    @GetMapping("/search")
    public Result searchGoods(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return goodsService.searchGoods(keyword, pageNum, pageSize);
    }
}