package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 商品图片上传控制器
 * 处理商品主图和多图的上传请求
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsFileController {

    private final FileUploadUtils fileUploadUtils;

    @Autowired
    public GoodsFileController(FileUploadUtils fileUploadUtils) {
        this.fileUploadUtils = fileUploadUtils;
    }

    /**
     * 上传商品图片
     * @param file 图片文件
     * @return 包含图片访问路径的结果
     */
    @PostMapping("/upload-image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 调用工具类上传文件
            String imageUrl = fileUploadUtils.uploadFile(file);
            // 返回图片访问路径
            return Result.success().put("imageUrl", imageUrl);
        } catch (IOException e) {
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}