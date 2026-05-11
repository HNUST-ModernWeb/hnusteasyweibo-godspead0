package com.hnusteasyweibo.service;

import com.hnusteasyweibo.dto.UserResponse;
import com.hnusteasyweibo.entity.User;

public interface UserService {
    UserResponse login(String username, String password);
    UserResponse register(User user);
    User getById(Long id);
    boolean update(User user);
}