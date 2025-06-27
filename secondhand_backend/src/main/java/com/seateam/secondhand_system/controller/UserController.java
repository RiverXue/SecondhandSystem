package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.User;
import com.seateam.secondhand_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

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
}