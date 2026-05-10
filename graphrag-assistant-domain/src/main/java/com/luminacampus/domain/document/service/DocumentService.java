package com.luminacampus.domain.document.service;

import com.luminacampus.domain.document.model.entity.DocumentEntity;
import com.luminacampus.domain.document.repository.IDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final IDocumentRepository documentRepository;

    public DocumentService(IDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void saveDocument(Long userId, String fileName, String subject) {
        DocumentEntity entity = DocumentEntity.builder()
                .userId(userId)
                .fileName(fileName)
                .subject(subject)
                .status(0)
                .build();
        documentRepository.save(entity);
    }

    public List<DocumentEntity> queryByUserId(Long userId) {
        return documentRepository.queryByUserId(userId);
    }

}
