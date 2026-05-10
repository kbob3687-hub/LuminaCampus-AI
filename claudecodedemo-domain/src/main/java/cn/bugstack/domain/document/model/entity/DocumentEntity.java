package cn.bugstack.domain.document.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {

    private Long id;
    private Long userId;
    private String fileName;
    private String subject;
    private Integer status;

}
