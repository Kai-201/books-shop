package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.BusinessException;
import com.bookshop.dto.UserRequest;
import com.bookshop.entity.User;
import com.bookshop.mapper.UserMapper;
import com.bookshop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getId));
    }

    @Override
    public Page<User> page(int page, int size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getLoginName, keyword)
                    .or().like(User::getUsername, keyword)
                    .or().like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getId);
        return userMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public User getById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void create(UserRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getLoginName, request.getLoginName()));
        if (count > 0) {
            throw new BusinessException("账号已存在");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        User user = new User();
        copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public void update(UserRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        User existing = userMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userMapper.updateById(user);
    }

    @Override
    public void delete(Integer id) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
    }

    private void copyProperties(UserRequest request, User user) {
        user.setLoginName(request.getLoginName());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
    }
}
