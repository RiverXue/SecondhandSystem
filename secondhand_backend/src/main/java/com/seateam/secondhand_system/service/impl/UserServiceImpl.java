package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.utils.JwtUtils;
import com.seateam.secondhand_system.entity.User;
import com.seateam.secondhand_system.mapper.UserMapper;
import com.seateam.secondhand_system.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @description 针对表【user(用户表)】的数据库操作Service实现，包含业务逻辑
 * @createDate 2025-06-17 10:50:27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 日志记录器
     */

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    // 数据库操作接口
    @Autowired
    private UserMapper userMapper;
    // 密码加密
    @Autowired
    private PasswordEncoder passwordEncoder;
    // JWT工具类
    @Autowired
    private JwtUtils jwtUtils;
    // 缓存操作
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
     * 用户注册
     *
     * @param user 用户
     * @return {@link Result}
     */
    @Override
    public Result register(User user) {
        // 1. 验证参数（用户名密码非空、密码复杂度、手机号格式）
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }
        // 密码复杂度验证：至少8位，包含字母和数字
        if (user.getPassword().length() < 8 || !user.getPassword().matches(".*[A-Za-z].*") || !user.getPassword().matches(".*\\d.*")) {
            return Result.error("密码至少8位，且包含字母和数字");
        }
        // 手机号格式验证(以 1 开头，第二位为 3-9，总共 11 位数字的字符串)
        if (user.getPhone() != null && !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 2. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 3. 密码加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 4. 设置默认角色和创建时间
        if (user.getRole() == null) {
            user.setRole("user");
        }
        user.setCreateTime(new Date());

        // 5. 保存用户
        try {
            int rows = userMapper.insert(user);
            if (rows > 0) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.error("注册失败：系统异常");
        }
    }

    /**
     * 用户登录
     *
     * @param user 用户
     * @return {@link Result}
     */
    @Override
    public Result login(User user) {
        // 1. 验证参数
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }

        // 2. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User dbUser;
        try {
            dbUser = userMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return Result.error("登录失败：系统异常");
        }
        if (dbUser == null) {
            return Result.error("用户名或密码错误");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 4. 生成JWT
        String accessToken = jwtUtils.generateAccessToken(dbUser.getId().toString());
        String refreshToken = jwtUtils.generateRefreshToken(dbUser.getId().toString());

        // 5. 返回结果
        return Result.success("登录成功")
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .put("user", dbUser);
    }

    /**
     * 获取当前用户信息
     *
     * @return {@link Result}     当前用户信息
     */
    @Override
    public Result getCurrentUserInfo() {
        // 1. 从JWT中获取用户ID
        Long userId = getCurrentUserId();

        // 2. 查询用户信息
        // 先从缓存获取用户信息
        String cacheKey = "user:info:" + userId;
        User user = (User) redisTemplate.opsForValue().get(cacheKey);
        try {
            if (user == null) {
                // 缓存未命中，查询数据库
                user = userMapper.selectById(userId);
                // 查询结果存入缓存，设置30分钟过期
                redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户信息失败", e);
            return Result.error("获取用户信息失败：系统异常");
        }
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 返回用户信息（隐藏密码）
        user.setPassword(null);
        return Result.success().put("user", user);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return {@link Result}
     */
    @Override
    public Result refreshToken(String refreshToken) {
        try {
            if (!jwtUtils.validateToken(refreshToken)) {
                return Result.error("刷新令牌无效");
            }
            String userId = jwtUtils.getUserIdFromToken(refreshToken);
            // 验证刷新令牌是否与Redis中存储的一致
            String storedToken = (String) redisTemplate.opsForValue().get("refresh_token:" + userId);
            if (!refreshToken.equals(storedToken)) {
                return Result.error("刷新令牌已失效");
            }
            // 生成新的访问令牌
            String newAccessToken = jwtUtils.generateAccessToken(userId);
            return Result.success("令牌刷新成功").put("accessToken", newAccessToken);
        } catch (Exception e) {
            log.error("令牌刷新失败", e);
            return Result.error("令牌刷新失败：系统异常");
        }
    }

    /**
     * 注销
     *
     * @param request 请求
     * @return {@link Result}
     */
    @Override
    public Result logout(HttpServletRequest request) {
        try {
            String token = jwtUtils.getTokenFromRequest(request);
            if (token != null) {
                jwtUtils.blacklistToken(token);
                // 清除刷新令牌
                Long userId = getCurrentUserId();
                redisTemplate.delete("refresh_token:" + userId);
            }
            return Result.success("登出成功");
        } catch (Exception e) {
            log.error("登出失败", e);
            return Result.error("登出失败：系统异常");
        }
    }

    /**
     * 更新用户信息
     *
     * @param user 用户
     * @return {@link Result}
     */
    @Override
    public Result updateUserInfo(User user) {
        // 获取当前登录用户ID
        Long currentUserId = getCurrentUserId();

        // 手机号格式验证
        if (user.getPhone() == null || !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 使用UpdateWrapper更新phone字段
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", currentUserId)
                .set("phone", user.getPhone());

        int rows = userMapper.update(null, updateWrapper);
        if (rows > 0) {
            // 清除缓存
            redisTemplate.delete("user:info:" + currentUserId);
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
}




