package com.luminacampus.domain.conversation.service;

import com.luminacampus.domain.conversation.model.entity.ConversationEntity;
import com.luminacampus.domain.conversation.repository.IConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final IConversationRepository conversationRepository;

    public ConversationService(IConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public void saveConversation(Long userId, String subject, String question, String answer) {
        ConversationEntity entity = ConversationEntity.builder()
                .userId(userId)
                .subject(subject)
                .question(question)
                .answer(answer)
                .build();
        conversationRepository.save(entity);
    }

    public List<ConversationEntity> queryByUserId(Long userId) {
        return conversationRepository.queryByUserId(userId);
    }

}
