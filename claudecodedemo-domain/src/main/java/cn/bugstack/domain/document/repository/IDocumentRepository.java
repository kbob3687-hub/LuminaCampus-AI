package cn.bugstack.domain.document.repository;

import cn.bugstack.domain.document.model.entity.DocumentEntity;

import java.util.List;

public interface IDocumentRepository {

    void save(DocumentEntity documentEntity);

    List<DocumentEntity> queryByUserId(Long userId);

}
