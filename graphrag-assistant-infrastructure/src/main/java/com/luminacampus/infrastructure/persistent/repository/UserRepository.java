package com.luminacampus.infrastructure.persistent.repository;

import com.luminacampus.domain.user.model.entity.UserEntity;
import com.luminacampus.domain.user.repository.IUserRepository;
import com.luminacampus.infrastructure.persistent.dao.IUserDao;
import com.luminacampus.infrastructure.persistent.po.UserPO;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements IUserRepository {

    private final IUserDao userDao;

    public UserRepository(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void register(UserEntity userEntity) {
        UserPO userPO = UserPO.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .build();
        userDao.insert(userPO);
    }

    @Override
    public UserEntity queryByUsername(String username) {
        UserPO userPO = userDao.queryByUsername(username);
        if (userPO == null) {
            return null;
        }
        return UserEntity.builder()
                .id(userPO.getId())
                .username(userPO.getUsername())
                .password(userPO.getPassword())
                .build();
    }

}
