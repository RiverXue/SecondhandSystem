package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.entity.User;
import com.seateam.secondhand_system.service.UserService;
import com.seateam.secondhand_system.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author HK
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-06-17 10:50:27
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




