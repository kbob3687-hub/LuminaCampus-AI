package com.luminacampus.domain.document.model.aggregate;

import com.luminacampus.domain.document.model.entity.DocumentEntity;
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
