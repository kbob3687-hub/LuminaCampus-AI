package com.luminacampus.infrastructure.persistent.dao;

import com.luminacampus.infrastructure.persistent.po.DocumentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IDocumentDao {

    void insert(DocumentPO documentPO);

    List<DocumentPO> queryByUserId(@Param("userId") Long userId);

}
