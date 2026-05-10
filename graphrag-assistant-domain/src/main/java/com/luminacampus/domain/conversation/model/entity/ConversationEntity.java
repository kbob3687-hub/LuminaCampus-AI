package com.luminacampus.domain.conversation.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEntity {

    private Long id;
    private Long userId;
    private String subject;
    private String question;
    private String answer;

}
