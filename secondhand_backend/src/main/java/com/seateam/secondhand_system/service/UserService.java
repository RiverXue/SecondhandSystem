package com.seateam.secondhand_system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return {@link Result}
     */
    Result getUserById(Integer userId);
    
    /**
     * 用户注册
     *
     * @param user 用户
     * @return {@link Result}
     */
    Result register(User user);

    /**
     * 用户登录
     *
     * @param user 用户
     * @return {@link Result}
     */
    Result login(User user);

    /**
     * 获取当前用户信息
     *
     * @return {@link Result}     当前用户信息
     */
    Result getCurrentUserInfo();

    /**
     * 更新用户信息
     *
     * @param user 用户
     * @return {@link Result}
     */
    Result updateUserInfo(User user);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return {@link Result}
     */
    Result refreshToken(String refreshToken);

    /**
     * 注销
     *
     * @param request 请求
     * @return {@link Result}
     */
    Result logout(HttpServletRequest request);
}
