package com.bookshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.dto.UserRequest;
import com.bookshop.entity.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    Page<User> page(int page, int size, String keyword);

    User getById(Integer id);

    void create(UserRequest request);

    void update(UserRequest request);

    void delete(Integer id);
}
