package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.service.GoodsService;
import com.seateam.secondhand_system.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

/**
* @author HK
* @description 针对表【goods(商品表)】的数据库操作Service实现
* @createDate 2025-06-17 10:50:26
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService{

}




