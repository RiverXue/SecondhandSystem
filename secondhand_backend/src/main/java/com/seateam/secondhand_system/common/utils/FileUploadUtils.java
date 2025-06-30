package com.seateam.secondhand_system.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理本地文件存储和访问路径生成
 */
@Component
public class FileUploadUtils {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 上传文件到本地服务器
     *
     * @param file 上传的文件
     * @return 文件访问路径
     * @throws IOException 当文件保存失败时抛出
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 1. 验证文件是否为空
        if (file.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }

        // 2. 创建上传目录（如果不存在）
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean mkdirsSuccess = uploadDir.mkdirs();
            if (!mkdirsSuccess) {
                throw new IOException("创建上传目录失败: " + uploadPath);
            }
        }

        // 3. 生成唯一文件名（避免重名）
        String originalFilename = file.getOriginalFilename();  // 获取原始文件名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));  // 获取文件扩展名
        String uniqueFileName = UUID.randomUUID() + fileExtension;  // 生成唯一文件名, 加上扩展名

        // 4. 保存文件到本地
        File destFile = new File(uploadDir, uniqueFileName);  // 构建完整的文件路径
        file.transferTo(destFile);  // 保存文件

        // 5. 返回文件访问路径（相对路径）
        return uniqueFileName;
    }
}