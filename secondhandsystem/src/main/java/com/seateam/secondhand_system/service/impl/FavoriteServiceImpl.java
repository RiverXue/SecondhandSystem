package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.entity.Favorite;
import com.seateam.secondhand_system.service.FavoriteService;
import com.seateam.secondhand_system.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;

/**
* @author HK
* @description 针对表【favorite(收藏表)】的数据库操作Service实现
* @createDate 2025-06-17 10:50:26
*/
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
    implements FavoriteService{

}




