package com.hepiplant.backend.service;


import com.hepiplant.backend.entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PostService {
    List<Post> getAll();
    Post getById(Long id);
}
