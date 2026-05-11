package com.hnusteasyweibo.service;

public interface LikeService {
    boolean toggleLike(Long postId, Long userId);
    boolean isLiked(Long postId, Long userId);
    int getLikesCount(Long postId);
}