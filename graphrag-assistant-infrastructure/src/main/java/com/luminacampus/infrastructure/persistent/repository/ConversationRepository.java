package com.luminacampus.infrastructure.persistent.repository;

import com.luminacampus.domain.conversation.model.entity.ConversationEntity;
import com.luminacampus.domain.conversation.repository.IConversationRepository;
import com.luminacampus.infrastructure.persistent.dao.IConversationDao;
import com.luminacampus.infrastructure.persistent.po.ConversationPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ConversationRepository implements IConversationRepository {

    private final IConversationDao conversationDao;

    public ConversationRepository(IConversationDao conversationDao) {
        this.conversationDao = conversationDao;
    }

    @Override
    public void save(ConversationEntity conversationEntity) {
        ConversationPO conversationPO = ConversationPO.builder()
                .userId(conversationEntity.getUserId())
                .subject(conversationEntity.getSubject())
                .question(conversationEntity.getQuestion())
                .answer(conversationEntity.getAnswer())
                .build();
        conversationDao.insert(conversationPO);
    }

    @Override
    public List<ConversationEntity> queryByUserId(Long userId) {
        return conversationDao.queryByUserId(userId).stream()
                .map(po -> ConversationEntity.builder()
                        .id(po.getId())
                        .userId(po.getUserId())
                        .subject(po.getSubject())
                        .question(po.getQuestion())
                        .answer(po.getAnswer())
                        .build())
                .collect(Collectors.toList());
    }

}
