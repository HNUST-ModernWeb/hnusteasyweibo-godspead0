package com.hnusteasyweibo.entity;

import java.io.Serializable;

public class PostTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long postId;
    private Long topicId;

    public PostTopic() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}