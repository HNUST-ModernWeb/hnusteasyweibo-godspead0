package com.hnusteasyweibo.controller;

import com.hnusteasyweibo.entity.Topic;
import com.hnusteasyweibo.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping("/hot")
    public ResponseEntity<List<Topic>> getHotTopics(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(topicService.getHotTopics(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getById(@PathVariable Long id) {
        Topic topic = topicService.findById(id);
        if (topic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topic);
    }

    @GetMapping("/search")
    public ResponseEntity<Topic> searchByName(@RequestParam String name) {
        Topic topic = topicService.findByName(name);
        if (topic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topic);
    }

    @PostMapping("/create")
    public ResponseEntity<Topic> createTopic(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.getOrDefault("description", "");
        Topic topic = topicService.createTopic(name, description);
        return ResponseEntity.ok(topic);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Topic>> getTopicsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(topicService.getTopicsByPostId(postId));
    }
}