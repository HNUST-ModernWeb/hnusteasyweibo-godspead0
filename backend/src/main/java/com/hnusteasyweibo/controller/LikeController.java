package com.hnusteasyweibo.controller;

import com.hnusteasyweibo.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam Long postId, 
            @RequestParam Long userId) {
        boolean liked = likeService.toggleLike(postId, userId);
        int count = likeService.getLikesCount(postId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @RequestParam Long postId, 
            @RequestParam Long userId) {
        boolean liked = likeService.isLiked(postId, userId);
        int count = likeService.getLikesCount(postId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/{postId}")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikesCount(postId));
    }
}