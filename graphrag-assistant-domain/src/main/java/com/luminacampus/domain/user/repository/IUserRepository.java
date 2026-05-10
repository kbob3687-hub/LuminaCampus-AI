package com.luminacampus.domain.user.repository;

import com.luminacampus.domain.user.model.entity.UserEntity;

public interface IUserRepository {

    void register(UserEntity userEntity);

    UserEntity queryByUsername(String username);

}
