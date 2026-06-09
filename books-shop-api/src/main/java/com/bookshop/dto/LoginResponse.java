package com.bookshop.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private Integer id;
    private String loginName;
    private String username;
    private String role;
}
