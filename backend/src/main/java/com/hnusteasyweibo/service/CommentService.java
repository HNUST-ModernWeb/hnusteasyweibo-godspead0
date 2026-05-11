package com.hnusteasyweibo.service;

import com.hnusteasyweibo.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPostId(Long postId);
    Comment createComment(Comment comment);
    boolean delete(Long id);
    List<Comment> getReplies(Long rootId);
}
