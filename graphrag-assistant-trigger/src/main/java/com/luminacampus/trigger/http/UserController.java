package com.luminacampus.trigger.http;

import com.luminacampus.domain.user.model.valobj.LoginVO;
import com.luminacampus.domain.user.service.UserService;
import com.luminacampus.types.common.Constants;
import com.luminacampus.types.enums.ResponseCode;
import com.luminacampus.types.exception.AppException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "用户名和密码不能为空");
        }
        userService.register(username, password);
        Map<String, Object> result = new HashMap<>();
        result.put("code", ResponseCode.SUCCESS.getCode());
        result.put("info", "注册成功");
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "用户名和密码不能为空");
        }
        LoginVO loginVO = userService.login(username, password);
        Map<String, Object> result = new HashMap<>();
        result.put("code", ResponseCode.SUCCESS.getCode());
        result.put("info", "登录成功");
        result.put("data", loginVO);
        return result;
    }

}
