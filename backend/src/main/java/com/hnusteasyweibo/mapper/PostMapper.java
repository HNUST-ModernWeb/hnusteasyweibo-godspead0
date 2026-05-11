package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.Post;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PostMapper {
    @Select("SELECT * FROM post WHERE id = #{id}")
    Post findById(Long id);
    
    @Select("SELECT * FROM post ORDER BY created_at DESC")
    List<Post> findAll();
    
    @Select("SELECT * FROM post WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Post> findByUserId(Long userId);

    @Insert("INSERT INTO post(user_id, content, image_urls, like_count, comment_count, repost_count, created_at, updated_at) " +
            "VALUES(#{userId}, #{content}, #{imageUrls}, #{likeCount}, #{commentCount}, #{repostCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);

    @Update("UPDATE post SET content = #{content}, image_urls = #{imageUrls}, like_count = #{likeCount}, " +
            "comment_count = #{commentCount}, repost_count = #{repostCount}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Post post);

    @Delete("DELETE FROM post WHERE id = #{id}")
    int delete(Long id);
}