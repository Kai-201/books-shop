package com.bookshop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends LoginRequest {

    @NotBlank(message = "昵称不能为空")
    private String username;
}
