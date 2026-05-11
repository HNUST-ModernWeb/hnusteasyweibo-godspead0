package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.Notification;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NotificationMapper {
    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification findById(Long id);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<Notification> findByUserId(Long userId, int limit);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = false")
    int countUnread(Long userId);

    @Insert("INSERT INTO notification(user_id, from_user_id, type, target_id, content, is_read, created_at) " +
            "VALUES(#{userId}, #{fromUserId}, #{type}, #{targetId}, #{content}, #{isRead}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Update("UPDATE notification SET is_read = true WHERE user_id = #{userId}")
    int markAllAsRead(Long userId);

    @Update("UPDATE notification SET is_read = true WHERE id = #{id}")
    int markAsRead(Long id);
}