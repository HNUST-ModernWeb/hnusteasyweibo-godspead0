package com.hnusteasyweibo.service;

import com.hnusteasyweibo.entity.Notification;

import java.util.List;

public interface NotificationService {
    void createLikeNotification(Long userId, Long fromUserId, Long postId);
    void createCommentNotification(Long userId, Long fromUserId, Long postId);
    void createFollowNotification(Long userId, Long fromUserId);
    List<Notification> getNotifications(Long userId, int limit);
    int getUnreadCount(Long userId);
    void markAllAsRead(Long userId);
    void markAsRead(Long id);
}