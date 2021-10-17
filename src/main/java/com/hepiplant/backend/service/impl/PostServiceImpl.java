package com.hepiplant.backend.service.impl;


import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.PostService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.helper.Utils.convertToLocalDate;
import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class PostServiceImpl implements PostService {

    private static final int TAGS_AMOUNT = 5;

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BeanValidator beanValidator;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, UserRepository userRepository, BeanValidator beanValidator) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public PostDto create(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setBody(postDto.getBody());
        if(postDto.getTags()!=null) {
            addTagsToPost(post, postDto.getTags());
        }
        post.setPhoto(postDto.getPhoto());
        // not null
        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for id " + postDto.getUserId()));
        post.setUser(user);
        // Can it be null?
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + postDto.getCategoryId()));
        post.setCategory(category);
        post.setComments(new ArrayList<>());
        beanValidator.validate(post);
        Post savedPost = postRepository.save(post);
        return mapToDto(savedPost);
    }

    @Override
    public List<PostDto> getAll(Date startDate, Date endDate) {
        if (startDate == null || endDate == null){ // todo maybe change
            return postRepository.findAll().stream()
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
        } else {
            return postRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate)).stream()
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PostDto> getAllByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + categoryId));
        return postRepository.findAllByCategory(category).stream()
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        return postRepository.findAllByUser(user).stream().map(DtoMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getAllByTag(String tag) {
        return postRepository.findAll().stream()
                .filter(p -> tag.equals(p.getTag1()) ||
                        tag.equals(p.getTag2()) ||
                        tag.equals(p.getTag3()) ||
                        tag.equals(p.getTag4()) ||
                        tag.equals(p.getTag5()))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id "+id));
        return mapToDto(post);
    }

    @Override
    public PostDto update(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id "+id));
        if(postDto.getTitle() != null){
            post.setTitle(postDto.getTitle());
        }
        if(postDto.getBody() != null){
            post.setBody(postDto.getBody());
        }
        if(postDto.getTags() != null && !postDto.getTags().isEmpty()){
            addTagsToPost(post, postDto.getTags());
        }
        if(postDto.getPhoto() !=null){
            post.setPhoto(postDto.getPhoto());
        }
        if(postDto.getUserId() != null){
            throw new ImmutableFieldException("Cannot change User for Post!");
        }
        if(postDto.getCategoryId() != null){
            Category category = categoryRepository.findById(postDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + postDto.getCategoryId()));
            post.setCategory(category);
        }
        beanValidator.validate(post);
        Post savedPost = postRepository.save(post);
        return mapToDto(savedPost);
    }

    @Override
    public String delete(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isEmpty()){
            return "No post with id = " + id;
        }
        postRepository.delete(post.get());
        return "Successfully deleted the post with id = "+ id;
    }

    private void addTagsToPost(Post post, List<String> tags) {
        for(int i = 0; i<TAGS_AMOUNT; i++){
            String tag = null;
            if(i < tags.size()){
                tag = tags.get(i);
            }
            switch (i){
                case 0:
                    post.setTag1(tag); break;
                case 1:
                    post.setTag2(tag); break;
                case 2:
                    post.setTag3(tag); break;
                case 3:
                    post.setTag4(tag); break;
                case 4:
                    post.setTag5(tag); break;
            }
        }
    }
}
