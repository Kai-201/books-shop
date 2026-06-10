package com.bookshop.dto;

import com.bookshop.common.validation.CreateGroup;
import com.bookshop.common.validation.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserRequest {

    @NotNull(message = "用户ID不能为空", groups = UpdateGroup.class)  // 仅更新时需要
    private Integer id;

    @NotBlank(message = "登录账号不能为空", groups = CreateGroup.class)  // 仅新增时需要（更新时 loginName 不改）
    private String loginName;

    @NotBlank(message = "密码不能为空", groups = CreateGroup.class)
    @Size(min = 6, max = 20, message = "密码长度6-20位", groups = CreateGroup.class)
    private String password;

    @NotBlank(message = "昵称不能为空")  // 两个场景都需要，不指定 groups
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}