package com.luminacampus.domain.document.repository;

import com.luminacampus.domain.document.model.entity.DocumentEntity;

import java.util.List;

public interface IDocumentRepository {

    void save(DocumentEntity documentEntity);

    List<DocumentEntity> queryByUserId(Long userId);

}
