package com.hnusteasyweibo.mapper;

import com.hnusteasyweibo.entity.Topic;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TopicMapper {
    @Select("SELECT * FROM topic WHERE id = #{id}")
    Topic findById(Long id);

    @Select("SELECT * FROM topic WHERE name = #{name}")
    Topic findByName(String name);

    @Select("SELECT * FROM topic ORDER BY post_count DESC LIMIT #{limit}")
    List<Topic> findHotTopics(int limit);

    @Insert("INSERT INTO topic(name, description, post_count, created_at, updated_at) " +
            "VALUES(#{name}, #{description}, #{postCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Topic topic);

    @Update("UPDATE topic SET description = #{description}, post_count = #{postCount}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Topic topic);
}