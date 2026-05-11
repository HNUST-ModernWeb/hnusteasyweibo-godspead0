package com.hnusteasyweibo.service.impl;

import com.hnusteasyweibo.entity.Notification;
import com.hnusteasyweibo.mapper.NotificationMapper;
import com.hnusteasyweibo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void createLikeNotification(Long userId, Long fromUserId, Long postId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType("LIKE");
        notification.setTargetId(postId);
        notification.setContent("赞了你的帖子");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public void createCommentNotification(Long userId, Long fromUserId, Long postId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType("COMMENT");
        notification.setTargetId(postId);
        notification.setContent("评论了你的帖子");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public void createFollowNotification(Long userId, Long fromUserId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType("FOLLOW");
        notification.setTargetId(null);
        notification.setContent("关注了你");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public List<Notification> getNotifications(Long userId, int limit) {
        return notificationMapper.findByUserId(userId, limit);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }
}