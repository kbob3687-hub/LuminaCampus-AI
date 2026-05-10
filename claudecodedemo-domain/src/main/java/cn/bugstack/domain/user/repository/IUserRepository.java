package cn.bugstack.domain.user.repository;

import cn.bugstack.domain.user.model.entity.UserEntity;

public interface IUserRepository {

    void register(UserEntity userEntity);

    UserEntity queryByUsername(String username);

}
