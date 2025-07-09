package com.seateam.secondhand_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.Goods;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface GoodsService extends IService<Goods> {
    /**
     * 发布商品
     *
     * @param goods 商品信息
     * @return 操作结果
     */
    Result publishGoods(Goods goods);

    /**
     * 分页查询商品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 商品分页列表
     */
    Result getGoodsPage(Integer pageNum, Integer pageSize);

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    Result getGoodsDetail(Long id);

    /**
     * 搜索商品
     *
     * @param keyword  搜索关键词
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 搜索结果分页列表
     */
    Result searchGoods(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 发布商品（支持图片上传）
     */
    Result publishGoodsWithImages(String title, String description, BigDecimal price, String category, MultipartFile mainImage, MultipartFile[] images);

    /**
     * 获取用户发布的商品列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 商品分页列表
     */
    Result getMyPublishedGoods(Integer pageNum, Integer pageSize);

    /**
     * 删除商品
     * @param id 商品ID
     * @return 操作结果
     */
    Result deleteGoods(Long id);
}
