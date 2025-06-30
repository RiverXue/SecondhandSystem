package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.utils.FileUploadUtils;
import com.seateam.secondhand_system.entity.User;
import com.seateam.secondhand_system.service.UserService;
import com.seateam.secondhand_system.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final FileUploadUtils fileUploadUtils;

    @Autowired
    private UserService userService;

    public UserController(FileUploadUtils fileUploadUtils) {
        this.fileUploadUtils = fileUploadUtils;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        System.out.println("前端正在注册，传来的数据：" + user);
        return userService.register(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {

        System.out.println("前端正在登录，传来的数据：" + user);
        return userService.login(user);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        System.out.println("前端正在获取用户信息");
        return userService.getCurrentUserInfo();
    }

    @GetMapping("/{userId}")
    public Result getUserById(@PathVariable(name = "userId") Integer userId) {
        System.out.println("前端请求获取用户ID为" + userId + "的信息");
        return userService.getUserById(userId);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public Result updateUserInfo(@RequestBody User user) {

        System.out.println("前端传来的数据：" + user);
        return userService.updateUserInfo(user);
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh-token")
    public Result refreshToken(@RequestParam String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        return userService.logout(request);
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info("Received Authorization header: {}", token != null ? token.substring(0, 10) + "..." : "null");
        return userService.uploadAvatar(file);
    }
}