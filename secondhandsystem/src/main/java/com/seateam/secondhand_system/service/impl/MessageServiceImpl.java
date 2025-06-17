package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.entity.Message;
import com.seateam.secondhand_system.service.MessageService;
import com.seateam.secondhand_system.mapper.MessageMapper;
import org.springframework.stereotype.Service;

/**
* @author HK
* @description 针对表【message(留言表)】的数据库操作Service实现
* @createDate 2025-06-17 10:50:27
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

}




