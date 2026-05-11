package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.Like;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    @Select("SELECT * FROM likes WHERE post_id = #{postId} AND user_id = #{userId}")
    Like findByPostIdAndUserId(Long postId, Long userId);

    @Insert("INSERT INTO likes(post_id, user_id, created_at) VALUES(#{postId}, #{userId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Like like);

    @Delete("DELETE FROM likes WHERE post_id = #{postId} AND user_id = #{userId}")
    int deleteByPostIdAndUserId(Long postId, Long userId);

    @Select("SELECT COUNT(*) FROM likes WHERE post_id = #{postId}")
    int countByPostId(Long postId);
}