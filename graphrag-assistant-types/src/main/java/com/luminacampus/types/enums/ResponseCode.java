package com.luminacampus.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    USER_NOT_EXISTS("B0001", "用户不存在"),
    USER_PASSWORD_ERROR("B0002", "密码错误"),
    TOKEN_EXPIRED("B0003", "Token过期"),
    ;

    private String code;
    private String info;

}
