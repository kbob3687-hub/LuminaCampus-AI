package cn.bugstack.domain.document.model.aggregate;

import cn.bugstack.domain.document.model.entity.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentAggregate {

    private DocumentEntity documentEntity;

}
