package com.luminacampus.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {

    private String question;
    private String subject;
    private String docId;

}
