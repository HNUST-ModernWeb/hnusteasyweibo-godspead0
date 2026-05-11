package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment findById(Long id);

    @Select("SELECT * FROM comment WHERE post_id = #{postId} ORDER BY created_at ASC")
    List<Comment> findByPostId(Long postId);

    @Select("SELECT * FROM comment WHERE root_id = #{rootId} OR id = #{rootId} ORDER BY created_at ASC")
    List<Comment> findRepliesByRootId(Long rootId);

    @Select("SELECT * FROM comment WHERE post_id = #{postId} AND (parent_id IS NULL OR parent_id = 0) ORDER BY created_at DESC")
    List<Comment> findRootCommentsByPostId(Long postId);

    @Insert("INSERT INTO comment(post_id, user_id, parent_id, root_id, content, reply_count, created_at, updated_at) " +
            "VALUES(#{postId}, #{userId}, #{parentId}, #{rootId}, #{content}, #{replyCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Update("UPDATE comment SET reply_count = reply_count + 1 WHERE id = #{id}")
    int incrementReplyCount(Long id);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT * FROM comment WHERE user_id = #{userId}")
    List<Comment> findByUserId(Long userId);
}
