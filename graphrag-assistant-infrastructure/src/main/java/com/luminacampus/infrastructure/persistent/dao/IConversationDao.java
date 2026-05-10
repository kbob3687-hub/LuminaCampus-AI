package com.luminacampus.infrastructure.persistent.dao;

import com.luminacampus.infrastructure.persistent.po.ConversationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IConversationDao {

    void insert(ConversationPO conversationPO);

    List<ConversationPO> queryByUserId(@Param("userId") Long userId);

}
