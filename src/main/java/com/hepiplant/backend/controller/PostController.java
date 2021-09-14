package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> addPost(@RequestBody PostDto postDto){
        return ResponseEntity.ok().body(postService.create(postDto));
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getPosts(){
        return ResponseEntity.ok().body(postService.getAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok().body(postService.getAllByCategory(categoryId));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(postService.getAllByUser(userId));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostDto>> getPostsByTag(@PathVariable String tag){
        return ResponseEntity.ok().body(postService.getAllByTag(tag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id){
        return  ResponseEntity.ok().body(postService.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> update(@PathVariable Long id, @RequestBody PostDto postDto){
        return  ResponseEntity.ok().body(postService.update(id, postDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(postService.delete(id));
    }
}
