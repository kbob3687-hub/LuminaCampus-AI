package com.luminacampus.infrastructure.persistent.dao;

import com.luminacampus.infrastructure.persistent.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IUserDao {

    void insert(UserPO userPO);

    UserPO queryByUsername(@Param("username") String username);

}
