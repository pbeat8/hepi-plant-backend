package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.service.PostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<PostDto> addPost(@RequestBody PostDto postDto){
        return ResponseEntity.ok().body(postService.create(postDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<PostDto>> getPosts(@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate){
        return ResponseEntity.ok().body(postService.getAll(startDate, endDate));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok().body(postService.getAllByCategory(categoryId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(postService.getAllByUser(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostDto>> getPostsByTag(@PathVariable String tag){
        return ResponseEntity.ok().body(postService.getAllByTag(tag));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id){
        return  ResponseEntity.ok().body(postService.getById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> update(@PathVariable Long id, @RequestBody PostDto postDto){
        return  ResponseEntity.ok().body(postService.update(id, postDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(postService.delete(id));
    }
}
