package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.PostTopic;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PostTopicMapper {
    @Select("SELECT * FROM post_topic WHERE post_id = #{postId}")
    List<PostTopic> findByPostId(Long postId);

    @Select("SELECT * FROM post_topic WHERE topic_id = #{topicId}")
    List<PostTopic> findByTopicId(Long topicId);

    @Insert("INSERT INTO post_topic(post_id, topic_id) VALUES(#{postId}, #{topicId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PostTopic postTopic);

    @Delete("DELETE FROM post_topic WHERE post_id = #{postId}")
    int deleteByPostId(Long postId);

    @Delete("DELETE FROM post_topic WHERE post_id = #{postId} AND topic_id = #{topicId}")
    int deleteByPostIdAndTopicId(Long postId, Long topicId);
}