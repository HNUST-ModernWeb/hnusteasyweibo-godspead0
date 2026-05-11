package com.hnusteasyweibo.service.impl;

import com.hnusteasyweibo.entity.PostTopic;
import com.hnusteasyweibo.entity.Topic;
import com.hnusteasyweibo.mapper.PostTopicMapper;
import com.hnusteasyweibo.mapper.TopicMapper;
import com.hnusteasyweibo.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    
    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private PostTopicMapper postTopicMapper;

    @Override
    public Topic createTopic(String name, String description) {
        Topic existing = topicMapper.findByName(name);
        if (existing != null) {
            return existing;
        }
        
        Topic topic = new Topic();
        topic.setName(name);
        topic.setDescription(description);
        topic.setPostCount(0);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        topicMapper.insert(topic);
        return topic;
    }

    @Override
    public Topic findById(Long id) {
        return topicMapper.findById(id);
    }

    @Override
    public Topic findByName(String name) {
        return topicMapper.findByName(name);
    }

    @Override
    public List<Topic> getHotTopics(int limit) {
        return topicMapper.findHotTopics(limit);
    }

    @Override
    public void addTopicToPost(Long postId, String topicName) {
        Topic topic = topicMapper.findByName(topicName);
        if (topic == null) {
            topic = createTopic(topicName, "");
        }
        
        final Topic finalTopic = topic;
        
        // 检查是否已关联
        List<PostTopic> existing = postTopicMapper.findByPostId(postId);
        boolean alreadyExists = existing.stream().anyMatch(pt -> pt.getTopicId().equals(finalTopic.getId()));
        
        if (!alreadyExists) {
            PostTopic postTopic = new PostTopic();
            postTopic.setPostId(postId);
            postTopic.setTopicId(finalTopic.getId());
            postTopicMapper.insert(postTopic);
            
            // 更新话题帖子数量
            finalTopic.setPostCount(finalTopic.getPostCount() + 1);
            finalTopic.setUpdatedAt(LocalDateTime.now());
            topicMapper.update(finalTopic);
        }
    }

    @Override
    public void addTopicsToPost(Long postId, List<String> topicNames) {
        for (String topicName : topicNames) {
            addTopicToPost(postId, topicName);
        }
    }

    @Override
    public List<Topic> getTopicsByPostId(Long postId) {
        List<PostTopic> postTopics = postTopicMapper.findByPostId(postId);
        List<Topic> topics = new ArrayList<>();
        for (PostTopic pt : postTopics) {
            Topic topic = topicMapper.findById(pt.getTopicId());
            if (topic != null) {
                topics.add(topic);
            }
        }
        return topics;
    }
}