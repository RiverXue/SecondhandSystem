package com.seateam.secondhand_system.service;

import com.seateam.secondhand_system.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HK
* @description 针对表【goods(商品表)】的数据库操作Service
* @createDate 2025-06-17 10:50:27
*/
import com.seateam.secondhand_system.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GoodsService extends IService<Goods> {
    /**
     * 发布商品
     * @param goods 商品信息
     * @return 操作结果
     */
    Result publishGoods(Goods goods);

    /**
     * 分页查询商品列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 商品分页列表
     */
    Result getGoodsPage(Integer pageNum, Integer pageSize);

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    Result getGoodsDetail(Long id);

    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 搜索结果分页列表
     */
    Result searchGoods(String keyword, Integer pageNum, Integer pageSize);
}
