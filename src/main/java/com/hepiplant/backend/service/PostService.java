package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.PostDto;

import java.util.Date;
import java.util.List;

public interface PostService {
    PostDto create(PostDto postDto);
    List<PostDto> getAll(Date startDate, Date endDate);
    List<PostDto> getAllByCategory(Long categoryId);
    List<PostDto> getAllByUser(Long userId);
    List<PostDto> getAllByTag(String tag); // todo by multiple tags?
    PostDto getById(Long id);
    PostDto update(Long id, PostDto postDto);
    String delete(Long id);

}
