package com.luminacampus.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPO {

    private Long id;
    private String username;
    private String password;
    private Date createTime;
    private Date updateTime;

}
