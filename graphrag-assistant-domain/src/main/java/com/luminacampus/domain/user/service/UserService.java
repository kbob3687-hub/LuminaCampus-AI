package com.luminacampus.domain.user.service;

import com.luminacampus.domain.user.model.entity.UserEntity;
import com.luminacampus.domain.user.model.valobj.LoginVO;
import com.luminacampus.domain.user.repository.IUserRepository;
import com.luminacampus.types.exception.AppException;
import com.luminacampus.types.enums.ResponseCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret:graphrag-assistant-secret-key-must-be-256bits!!}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void register(String username, String password) {
        UserEntity existing = userRepository.queryByUsername(username);
        if (existing != null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "用户名已存在");
        }
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.register(user);
    }

    public LoginVO login(String username, String password) {
        UserEntity user = userRepository.queryByUsername(username);
        if (user == null) {
            throw new AppException(ResponseCode.USER_NOT_EXISTS.getCode(), ResponseCode.USER_NOT_EXISTS.getInfo());
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(ResponseCode.USER_PASSWORD_ERROR.getCode(), ResponseCode.USER_PASSWORD_ERROR.getInfo());
        }
        String token = generateToken(user);
        return LoginVO.builder()
                .token(token)
                .username(username)
                .build();
    }

    private String generateToken(UserEntity user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

}
