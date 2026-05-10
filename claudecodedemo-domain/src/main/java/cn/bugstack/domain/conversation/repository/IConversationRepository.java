package cn.bugstack.domain.conversation.repository;

import cn.bugstack.domain.conversation.model.entity.ConversationEntity;

import java.util.List;

public interface IConversationRepository {

    void save(ConversationEntity conversationEntity);

    List<ConversationEntity> queryByUserId(Long userId);

}
