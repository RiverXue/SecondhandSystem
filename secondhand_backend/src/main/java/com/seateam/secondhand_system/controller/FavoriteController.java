package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.service.FavoriteService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏模块控制器
 */
@RestController
@RequestMapping("/api/favorite")
@Validated
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */

    @PostMapping("/add")
    public Result addFavorite(@RequestParam(name = "goodsId") @NotNull(message = "商品ID不能为空") Long goodsId) {
        System.out.println("前端传来的goodsId：" + goodsId + "，正在添加收藏");
        Long userId = getCurrentUserId();
        return favoriteService.addFavorite(userId, goodsId);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result removeFavorite(@RequestParam(name = "goodsId") @NotNull(message = "商品ID不能为空") Long goodsId) {
        System.out.println("前端传来的数据goodsId" + goodsId);
        Long userId = getCurrentUserId();
        return favoriteService.removeFavorite(userId, goodsId);
    }

    /**
     * 获取我的收藏列表
     */
    @GetMapping("/list")
    public Result getFavorites(@RequestParam(name = "pageNum", defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") @Min(value = 1, message = "每页条数不能小于1") @Max(value = 100, message = "每页条数不能大于100") Integer pageSize) {
        System.out.println("前端传来的数据：" + pageNum + "，" + pageSize + "，正在获取我的收藏列表");
        Long userId = getCurrentUserId();
        System.out.println("前端应该获取到的结果：" + favoriteService.getFavoritesByUserId(userId, pageNum, pageSize) + "的收藏列表");
        return favoriteService.getFavoritesByUserId(userId, pageNum, pageSize);
    }

    /**
     * 从安全上下文获取当前用户ID，用于标识用户
     *
     * @return 当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new RuntimeException("用户未登录");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的用户ID");
        }
    }
}