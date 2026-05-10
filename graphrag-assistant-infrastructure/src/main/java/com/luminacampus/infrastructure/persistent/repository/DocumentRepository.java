package com.luminacampus.infrastructure.persistent.repository;

import com.luminacampus.domain.document.model.entity.DocumentEntity;
import com.luminacampus.domain.document.repository.IDocumentRepository;
import com.luminacampus.infrastructure.persistent.dao.IDocumentDao;
import com.luminacampus.infrastructure.persistent.po.DocumentPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DocumentRepository implements IDocumentRepository {

    private final IDocumentDao documentDao;

    public DocumentRepository(IDocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    @Override
    public void save(DocumentEntity documentEntity) {
        DocumentPO documentPO = DocumentPO.builder()
                .userId(documentEntity.getUserId())
                .fileName(documentEntity.getFileName())
                .subject(documentEntity.getSubject())
                .status(documentEntity.getStatus())
                .build();
        documentDao.insert(documentPO);
    }

    @Override
    public List<DocumentEntity> queryByUserId(Long userId) {
        return documentDao.queryByUserId(userId).stream()
                .map(po -> DocumentEntity.builder()
                        .id(po.getId())
                        .userId(po.getUserId())
                        .fileName(po.getFileName())
                        .subject(po.getSubject())
                        .status(po.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}
