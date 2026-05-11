package com.hnusteasyweibo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Like implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;

    public Like() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}