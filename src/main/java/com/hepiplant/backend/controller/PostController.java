package com.hepiplant.backend.controller;

import com.hepiplant.backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<?> getPosts(){
        return ResponseEntity.ok().body(postService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id){
        return  ResponseEntity.ok().body(postService.getById(id));
    }
}
