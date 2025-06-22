package com.seateam.secondhand_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seateam.secondhand_system.entity.Favorite;
import org.apache.ibatis.annotations.Param;

/**
 * @author HK
 * @description 针对表【favorite(收藏表)】的数据库操作Mapper
 * @createDate 2025-06-17 10:50:26
 * @Entity com.seateam.secondhand_system.entity.Favorite
 */
public interface FavoriteMapper extends BaseMapper<Favorite> {
    // 根据用户ID和商品ID查询收藏记录
    Favorite selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}




