package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookshop.common.BusinessException;
import com.bookshop.common.LoginUser;
import com.bookshop.dto.LoginRequest;
import com.bookshop.dto.LoginResponse;
import com.bookshop.entity.Admin;
import com.bookshop.entity.User;
import com.bookshop.mapper.AdminMapper;
import com.bookshop.mapper.UserMapper;
import com.bookshop.service.AuthService;
import com.bookshop.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AdminMapper adminMapper, UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse adminLogin(LoginRequest request) {
        // 先根据登录名查找管理员
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getLoginName, request.getLoginName()));
        if (admin == null) {
            throw new BusinessException("管理员账号或密码错误");
        }
        // BCrypt 密码匹配
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("管理员账号或密码错误");
        }
        return buildResponse(admin.getId(), admin.getLoginName(), "管理员", "admin");
    }

    @Override
    public LoginResponse userLogin(LoginRequest request) {
        // 先根据登录名查找用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getLoginName, request.getLoginName()));
        if (user == null) {
            throw new BusinessException("用户账号或密码错误");
        }
        // BCrypt 密码匹配
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户账号或密码错误");
        }
        return buildResponse(user.getId(), user.getLoginName(), user.getUsername(), "user");
    }

    @Override
    public LoginResponse userRegister(LoginRequest request, String username) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getLoginName, request.getLoginName()));
        if (count > 0) {
            throw new BusinessException("账号已存在");
        }

        User user = new User();
        user.setLoginName(request.getLoginName());
        // BCrypt 加密存储密码
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(username != null ? username : request.getLoginName());
        userMapper.insert(user);

        return buildResponse(user.getId(), user.getLoginName(), user.getUsername(), "user");
    }

    private LoginResponse buildResponse(Integer id, String loginName, String username, String role) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(id);
        loginUser.setLoginName(loginName);
        loginUser.setRole(role);

        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(loginUser));
        response.setId(id);
        response.setLoginName(loginName);
        response.setUsername(username);
        response.setRole(role);
        return response;
    }
}
