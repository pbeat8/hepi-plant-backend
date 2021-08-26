package com.hepiplant.backend.service.impl;


import com.hepiplant.backend.entity.Post;

import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.service.PostService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    public PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}
