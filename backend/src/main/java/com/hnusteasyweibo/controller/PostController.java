package com.hnusteasyweibo.controller;

import com.hnusteasyweibo.entity.Post;
import com.hnusteasyweibo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @GetMapping("/{id}")
    public Post getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @PutMapping
    public boolean update(@RequestBody Post post) {
        return postService.update(post);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return postService.delete(id);
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUserId(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId);
    }
}