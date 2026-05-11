package com.hnusteasyweibo.service.impl;

import com.hnusteasyweibo.entity.Comment;
import com.hnusteasyweibo.entity.Post;
import com.hnusteasyweibo.entity.User;
import com.hnusteasyweibo.mapper.CommentMapper;
import com.hnusteasyweibo.mapper.PostMapper;
import com.hnusteasyweibo.mapper.UserMapper;
import com.hnusteasyweibo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        List<Comment> allComments = commentMapper.findByPostId(postId);
        Map<Long, User> userMap = getUserMap(allComments);

        List<Comment> rootComments = allComments.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> replyMap = allComments.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() != 0)
                .collect(Collectors.groupingBy(Comment::getParentId));

        for (Comment root : rootComments) {
            List<Comment> replies = replyMap.getOrDefault(root.getId(), new ArrayList<>());
            replies.forEach(reply -> {
                reply.setUser(userMap.get(reply.getUserId()));
                if (reply.getParentId() != null && reply.getParentId() != 0) {
                    Comment parent = allComments.stream()
                            .filter(c -> c.getId().equals(reply.getParentId()))
                            .findFirst().orElse(null);
                    if (parent != null) {
                        reply.setReplyToUser(userMap.get(parent.getUserId()));
                    }
                }
            });
            root.setReplies(replies);
            root.setUser(userMap.get(root.getUserId()));
        }

        rootComments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return rootComments;
    }

    @Override
    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setReplyCount(0);

        if (comment.getParentId() != null && comment.getParentId() != 0) {
            commentMapper.incrementReplyCount(comment.getParentId());
        } else {
            comment.setParentId(0L);
        }

        if (comment.getRootId() == null || comment.getRootId() == 0) {
            comment.setRootId(comment.getParentId() != null && comment.getParentId() != 0 ? comment.getParentId() : 0L);
        }

        commentMapper.insert(comment);

        Post post = postMapper.findById(comment.getPostId());
        if (post != null) {
            post.setCommentCount((post.getCommentCount() != null ? post.getCommentCount() : 0) + 1);
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.update(post);
        }

        Comment newComment = commentMapper.findById(comment.getId());
        User user = userMapper.findById(comment.getUserId());
        newComment.setUser(user);
        return newComment;
    }

    @Override
    public boolean delete(Long id) {
        Comment comment = commentMapper.findById(id);
        if (comment != null) {
            commentMapper.delete(id);

            if (comment.getParentId() != null && comment.getParentId() != 0) {
                Comment parent = commentMapper.findById(comment.getParentId());
                if (parent != null && parent.getReplyCount() != null && parent.getReplyCount() > 0) {
                    parent.setReplyCount(parent.getReplyCount() - 1);
                    parent.setUpdatedAt(LocalDateTime.now());
                    commentMapper.insert(parent);
                }
            }

            Post post = postMapper.findById(comment.getPostId());
            if (post != null && post.getCommentCount() != null && post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                post.setUpdatedAt(LocalDateTime.now());
                postMapper.update(post);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Comment> getReplies(Long rootId) {
        List<Comment> replies = commentMapper.findRepliesByRootId(rootId);
        Map<Long, User> userMap = getUserMap(replies);

        replies.forEach(reply -> {
            reply.setUser(userMap.get(reply.getUserId()));
            if (reply.getParentId() != null && reply.getParentId() != 0) {
                Comment parent = commentMapper.findById(reply.getParentId());
                if (parent != null) {
                    reply.setReplyToUser(userMap.get(parent.getUserId()));
                }
            }
        });

        return replies;
    }

    private Map<Long, User> getUserMap(List<Comment> comments) {
        Set<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = new HashMap<>();
        for (Long userId : userIds) {
            User user = userMapper.findById(userId);
            if (user != null) {
                userMap.put(userId, user);
            }
        }
        return userMap;
    }
}
