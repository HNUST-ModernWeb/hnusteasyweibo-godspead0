package com.hnusteasyweibo.controller;

import com.hnusteasyweibo.dto.UserResponse;
import com.hnusteasyweibo.entity.User;
import com.hnusteasyweibo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody User user) {
        UserResponse response = userService.login(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        UserResponse response = userService.register(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<Boolean> update(@RequestBody User user) {
        boolean success = userService.update(user);
        return ResponseEntity.ok(success);
    }
}