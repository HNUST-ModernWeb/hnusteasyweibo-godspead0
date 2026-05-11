package com.hnusteasyweibo.service.impl;

import com.hnusteasyweibo.entity.Like;
import com.hnusteasyweibo.entity.Post;
import com.hnusteasyweibo.mapper.LikeMapper;
import com.hnusteasyweibo.mapper.PostMapper;
import com.hnusteasyweibo.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeServiceImpl implements LikeService {
    
    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public boolean toggleLike(Long postId, Long userId) {
        Like existing = likeMapper.findByPostIdAndUserId(postId, userId);
        
        if (existing != null) {
            likeMapper.deleteByPostIdAndUserId(postId, userId);
            
            Post post = postMapper.findById(postId);
            if (post != null && post.getLikeCount() != null && post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
                post.setUpdatedAt(LocalDateTime.now());
                postMapper.update(post);
            }
            return false;
        } else {
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeMapper.insert(like);
            
            Post post = postMapper.findById(postId);
            if (post != null) {
                post.setLikeCount((post.getLikeCount() != null ? post.getLikeCount() : 0) + 1);
                post.setUpdatedAt(LocalDateTime.now());
                postMapper.update(post);
            }
            return true;
        }
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        return likeMapper.findByPostIdAndUserId(postId, userId) != null;
    }

    @Override
    public int getLikesCount(Long postId) {
        return likeMapper.countByPostId(postId);
    }
}