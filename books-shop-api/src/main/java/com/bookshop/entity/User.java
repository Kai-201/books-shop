package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_info")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String loginName;
    private String password;
    private String username;
    private String phone;
    private String email;
    private LocalDateTime createTime;
}
