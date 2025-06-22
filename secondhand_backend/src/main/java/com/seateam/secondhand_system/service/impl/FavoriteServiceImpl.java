package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.ResultCode;
import com.seateam.secondhand_system.entity.Favorite;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.mapper.FavoriteMapper;
import com.seateam.secondhand_system.mapper.GoodsMapper;
import com.seateam.secondhand_system.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author HK
 * @description 针对表【favorite(收藏表)】的数据库操作Service实现
 * @createDate 2025-06-17 10:50:26
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
        implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 添加收藏
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @return 操作结果
     */
    @Override
    public Result addFavorite(Long userId, Long goodsId) {
        // 检查是否已收藏
        Favorite existing = favoriteMapper.selectByUserIdAndGoodsId(userId, goodsId);
        if (existing != null) {
            return Result.error(ResultCode.ERROR, "已收藏该商品");
        }

        // 检查商品是否存在且在售
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.error(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (goods.getStatus() != 0) {
            return Result.error(ResultCode.ERROR, "只能收藏在售商品");
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        int rows = favoriteMapper.insert(favorite);

        return rows > 0 ? Result.success("收藏成功") : Result.error("收藏失败");
    }

    /**
     * 取消收藏
     *
     * @param userId  用户ID
     * @param goodsId 商品ID
     * @return 操作结果
     */
    @Override
    public Result removeFavorite(Long userId, Long goodsId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("goods_id", goodsId);
        int rows = favoriteMapper.delete(queryWrapper);

        if (rows == 0) {
            return Result.error(ResultCode.NOT_FOUND, "未找到收藏记录");
        }
        return Result.success("取消收藏成功");
    }

    /**
     * 获取用户收藏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 收藏列表
     */
    @Override
    public Result getFavoritesByUserId(Long userId, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("id");
        IPage<Favorite> favoritePage = favoriteMapper.selectPage(page, queryWrapper);

        // 获取收藏的商品ID列表
        List<Long> goodsIds = favoritePage.getRecords().stream()
                .map(Favorite::getGoodsId)
                .toList();

        // 查询商品详情（仅包含在售商品）
        List<Goods> goodsList;
        if (!goodsIds.isEmpty()) {
            QueryWrapper<Goods> goodsQuery = new QueryWrapper<>();
            goodsQuery.in("id", goodsIds);
            goodsList = goodsMapper.selectList(goodsQuery);
        } else {
            goodsList = Collections.emptyList();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", favoritePage.getTotal());
        data.put("favoritesList", goodsList);

        return Result.success().put("data", data);
    }
}




