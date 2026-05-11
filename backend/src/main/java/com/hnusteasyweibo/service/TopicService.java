package com.hnusteasyweibo.service;

import com.hnusteasyweibo.entity.Topic;

import java.util.List;

public interface TopicService {
    Topic createTopic(String name, String description);
    Topic findById(Long id);
    Topic findByName(String name);
    List<Topic> getHotTopics(int limit);
    void addTopicToPost(Long postId, String topicName);
    void addTopicsToPost(Long postId, List<String> topicNames);
    List<Topic> getTopicsByPostId(Long postId);
}