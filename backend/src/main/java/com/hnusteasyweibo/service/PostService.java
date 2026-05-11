package com.hnusteasyweibo.service;

import com.hnusteasyweibo.entity.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post createPost(Post post);
    Post getById(Long id);
    boolean update(Post post);
    boolean delete(Long id);
    List<Post> getPostsByUserId(Long userId);
}