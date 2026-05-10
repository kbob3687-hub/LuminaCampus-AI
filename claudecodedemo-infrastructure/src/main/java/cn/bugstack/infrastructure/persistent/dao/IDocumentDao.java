package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.DocumentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IDocumentDao {

    void insert(DocumentPO documentPO);

    List<DocumentPO> queryByUserId(@Param("userId") Long userId);

}
