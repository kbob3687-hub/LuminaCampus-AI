package com.luminacampus.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPO {

    private Long id;
    private Long userId;
    private String fileName;
    private String subject;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
