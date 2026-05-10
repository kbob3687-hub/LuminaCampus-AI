package com.luminacampus.domain.conversation.repository;

import com.luminacampus.domain.conversation.model.entity.ConversationEntity;

import java.util.List;

public interface IConversationRepository {

    void save(ConversationEntity conversationEntity);

    List<ConversationEntity> queryByUserId(Long userId);

}
