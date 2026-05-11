package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO user(username, password, nickname, avatar, bio, following_count, follower_count, post_count, created_at, updated_at) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{avatar}, #{bio}, #{followingCount}, #{followerCount}, #{postCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET nickname = #{nickname}, avatar = #{avatar}, bio = #{bio}, following_count = #{followingCount}, " +
            "follower_count = #{followerCount}, post_count = #{postCount}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(User user);
}