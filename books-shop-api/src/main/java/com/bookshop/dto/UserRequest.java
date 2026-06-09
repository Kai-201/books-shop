package com.bookshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRequest {

    private Integer id;

    @NotBlank(message = "登录账号不能为空")
    private String loginName;

    private String password;

    @NotBlank(message = "昵称不能为空")
    private String username;

    private String phone;
    private String email;
}
