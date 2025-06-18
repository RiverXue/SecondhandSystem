package com.seateam.secondhand_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.Favorite;

/**
 * @author HK
 * @description 针对表【favorite(收藏表)】的数据库操作Service
 * @createDate 2025-06-17 10:50:26
 */
public interface FavoriteService extends IService<Favorite> {
    /**
     * 添加收藏
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @return 操作结果
     */
    Result addFavorite(Long userId, Long goodsId);

    /**
     * 取消收藏
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @return 操作结果
     */
    Result removeFavorite(Long userId, Long goodsId);

    /**
     * 获取用户收藏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 收藏列表
     */
    Result getFavoritesByUserId(Long userId, Integer pageNum, Integer pageSize);
}
