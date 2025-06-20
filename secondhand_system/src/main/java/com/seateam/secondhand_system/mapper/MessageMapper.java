package com.seateam.secondhand_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seateam.secondhand_system.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {
    /**
     * 根据商品ID查询留言列表
     *
     * @param goodsId 商品ID
     * @return 留言列表
     */
    List<Message> selectByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 更新留言回复
     */
    int updateReply(@Param("messageId") Long messgaeId, @Param("replyContent") String replyContent, @Param("replyTime") Date replyTime);
}




