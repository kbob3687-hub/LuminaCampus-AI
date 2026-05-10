package cn.bugstack.trigger.http;

import cn.bugstack.domain.user.model.valobj.LoginVO;
import cn.bugstack.domain.user.service.UserService;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
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
