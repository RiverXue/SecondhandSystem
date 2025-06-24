package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.utils.FileUploadUtils;
import com.seateam.secondhand_system.common.utils.JwtUtils;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.mapper.GoodsMapper;
import com.seateam.secondhand_system.service.GoodsService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HK
 * @description 针对表【goods(商品表)】的数据库操作Service实现
 * @createDate 2025-06-17 10:50:26
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {

    private static final Logger log = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private FileUploadUtils fileUploadUtils;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Long userId = jwtUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    /**
     * 发布商品
     */
    @Override
    public Result publishGoods(Goods goods) {
        // 1. 验证参数
        if (!StringUtils.hasText(goods.getTitle()) || goods.getPrice() == null) {
            return Result.error("商品标题和价格不能为空");
        }

        // 2. 设置发布者ID和默认状态
        goods.setUserId(getCurrentUserId());
        goods.setStatus(0); // 0-在售
        goods.setCreateTime(new Date());

        // 3. 保存商品
        try {
            int rows = goodsMapper.insert(goods);
            if (rows > 0) {
                return Result.success("商品发布成功");
            } else {
                return Result.error("商品发布失败");
            }
        } catch (Exception e) {
            log.error("商品发布失败", e);
            return Result.error("商品发布失败：系统异常");
        }
    }

    /**
     * 发布商品（支持图片上传）
     */
    public Result publishGoodsWithImages(String title, String description, BigDecimal price, String category, MultipartFile mainImage, MultipartFile[] images) {
        // 1. 验证参数
        if (!StringUtils.hasText(title) || price == null) {
            return Result.error("商品标题和价格不能为空");
        }

        // 2. 创建商品对象并设置基本信息
        Goods goods = new Goods();
        goods.setTitle(title);
        goods.setDescription(description);
        goods.setPrice(price);
        goods.setCategory(category);
        goods.setUserId(getCurrentUserId());
        goods.setStatus(0); // 0-在售
        goods.setCreateTime(new Date());

        try {
            // 3. 处理主图上传
            if (mainImage != null && !mainImage.isEmpty()) {
                String mainImageUrl = fileUploadUtils.uploadFile(mainImage);
                if (StringUtils.isEmpty(mainImageUrl)) {
                    log.error("主图上传失败，返回URL为空");
                    return Result.error("主图上传失败");
                }
                goods.setImage(mainImageUrl);
                log.info("主图上传成功: {}", mainImageUrl);
            } else {
                log.warn("未上传主图文件");
                // 主图不是必填项，不返回错误
            }

            // 4. 处理多图上传
            if (images != null && images.length > 0) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = fileUploadUtils.uploadFile(image);
                        if (StringUtils.isEmpty(imageUrl)) {
                            log.error("多图上传失败，返回URL为空");
                            return Result.error("多图上传失败");
                        }
                        imageUrls.add(imageUrl);
                        log.info("多图上传成功: {}", imageUrl);
                    } else {
                        log.warn("跳过空的多图文件");
                    }
                }
                if (!imageUrls.isEmpty()) {
                    goods.setImages(objectMapper.writeValueAsString(imageUrls));
                } else {
                    log.warn("未上传任何有效多图文件");
                }
            } else {
                log.warn("未提供多图文件");
            }

            // 5. 保存商品
            int rows = goodsMapper.insert(goods);
            if (rows > 0) {
                log.info("商品保存成功，主图URL: {}, 多图URLs: {}", goods.getImage(), goods.getImages());
                return Result.success("商品发布成功")
                        .put("goodsId", goods.getId())
                        .put("image", goods.getImage())
                        .put("images", goods.getImages());

            } else {
                return Result.error("商品发布失败");
            }
        } catch (IOException e) {
            log.error("商品图片上传失败", e);
            return Result.error("商品发布失败：图片上传异常 - " + e.getMessage());
        }
    }

    /**
     * 商品分页列表
     */
    @Override
    public Result getGoodsPage(Integer pageNum, Integer pageSize) {
        // 1. 验证分页参数
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            pageNum = 1;
            pageSize = 10;
        }

        // 2. 分页查询商品列表（按发布时间倒序）
        Page<Goods> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        IPage<Goods> goodsPage = goodsMapper.selectPage(page, queryWrapper);

        return Result.success().put("total", goodsPage.getTotal()).put("list", goodsPage.getRecords());
    }

    /**
     * 获取商品详情
     */
    @Override
    public Result getGoodsDetail(Long id) {
        // 1. 验证商品ID
        if (id == null) {
            return Result.error("商品ID不能为空");
        }

        // 2. 查询商品详情
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            return Result.error("商品不存在");
        }

        return Result.success().put("goods", goods);
    }

    /**
     * 搜索商品
     */
    @Override
    public Result searchGoods(String keyword, Integer pageNum, Integer pageSize) {
        // 1. 验证参数
        if (!StringUtils.hasText(keyword)) {
            return Result.error("搜索关键词不能为空");
        }
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            pageNum = 1;
            pageSize = 10;
        }

        // 2. 分页搜索商品（标题或描述包含关键词）
        Page<Goods> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", keyword).or().like("description", keyword);
        queryWrapper.orderByDesc("create_time");
        IPage<Goods> goodsPage = goodsMapper.selectPage(page, queryWrapper);

        return Result.success().put("total", goodsPage.getTotal()).put("list", goodsPage.getRecords());
    }
}




