package com.hnusteasyweibo.service.impl;

import com.hnusteasyweibo.dto.UserResponse;
import com.hnusteasyweibo.entity.User;
import com.hnusteasyweibo.mapper.UserMapper;
import com.hnusteasyweibo.service.UserService;
import com.hnusteasyweibo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserResponse login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return buildUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse register(User user) {
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAvatar("");
        user.setBio("");
        user.setFollowingCount(0);
        user.setFollowerCount(0);
        user.setPostCount(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return buildUserResponse(user);
    }

    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean update(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.update(user) > 0;
    }

    private UserResponse buildUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setFollowingCount(user.getFollowingCount());
        response.setFollowerCount(user.getFollowerCount());
        response.setPostCount(user.getPostCount());
        response.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        return response;
    }
}