package com.bookshop.common;

import lombok.Data;

@Data
public class LoginUser {

    private Integer id;
    private String loginName;
    private String role;
}
