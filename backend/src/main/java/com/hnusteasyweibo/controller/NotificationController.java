package com.hnusteasyweibo.controller;

import com.hnusteasyweibo.entity.Notification;
import com.hnusteasyweibo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(notificationService.getNotifications(userId, limit));
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(@PathVariable Long userId) {
        Map<String, Integer> response = new HashMap<>();
        response.put("count", notificationService.getUnreadCount(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/all/{userId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}