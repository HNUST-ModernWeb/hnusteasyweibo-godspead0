package com.hnusteasyweibo.service.impl;

import com.hnusteasyweibo.entity.Post;
import com.hnusteasyweibo.mapper.PostMapper;
import com.hnusteasyweibo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    
    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Post> getAllPosts() {
        return postMapper.findAll();
    }

    @Override
    public Post createPost(Post post) {
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setRepostCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.insert(post);
        return postMapper.findById(post.getId());
    }

    @Override
    public Post getById(Long id) {
        return postMapper.findById(id);
    }

    @Override
    public boolean update(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.update(post) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return postMapper.delete(id) > 0;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        return postMapper.findByUserId(userId);
    }
}