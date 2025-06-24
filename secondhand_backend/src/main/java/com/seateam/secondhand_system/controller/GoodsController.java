package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 发布商品（支持图片上传）
     */
    @PostMapping(value = "/publish", consumes = "multipart/form-data")
    public Result publishGoods(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        return goodsService.publishGoodsWithImages(title, description, price, category, mainImage, images);
    }

    /**
     * 发布商品（纯数据接口，用于不需要上传图片的场景）
     */
    @PostMapping(value = "/publish", consumes = "application/json")
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