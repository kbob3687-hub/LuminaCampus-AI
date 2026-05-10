package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.document.model.entity.DocumentEntity;
import cn.bugstack.domain.document.repository.IDocumentRepository;
import cn.bugstack.infrastructure.persistent.dao.IDocumentDao;
import cn.bugstack.infrastructure.persistent.po.DocumentPO;
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
