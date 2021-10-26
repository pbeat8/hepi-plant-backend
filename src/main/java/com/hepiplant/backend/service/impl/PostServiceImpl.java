package com.hepiplant.backend.service.impl;


import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.dto.TagDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.PostComment;
import com.hepiplant.backend.entity.Tag;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.TagRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.PostService;
import com.hepiplant.backend.service.TagService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;
import static com.hepiplant.backend.util.ConversionUtils.convertToLocalDate;

@Service
public class PostServiceImpl implements PostService {

    private static final int TAGS_AMOUNT = 5;

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final BeanValidator beanValidator;
    private final TagService tagService;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, UserRepository userRepository, TagRepository tagRepository, BeanValidator beanValidator, TagService tagService) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.beanValidator = beanValidator;
        this.tagService = tagService;
    }

    @Override
    public PostDto create(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setBody(postDto.getBody());
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
        if(postDto.getTags()!=null) {
            addTagsToPost(post, postDto);
        }
        beanValidator.validate(post);
        Post savedPost = postRepository.save(post);
        return mapToDto(savedPost);
    }

    @Override
    public List<PostDto> getAll(Date startDate, Date endDate) {
        if (startDate == null || endDate == null){ // todo maybe change
            return postRepository.findAll().stream()
                    .sorted(Comparator.comparing(Post::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
        } else {
            return postRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate)).stream()
                    .sorted(Comparator.comparing(Post::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PostDto> getAllByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + categoryId));
        return postRepository.findAllByCategory(category).stream()
                .sorted(Comparator.comparing(Post::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        return postRepository.findAllByUser(user).stream()
                .sorted(Comparator.comparing(Post::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getAllByTag(String tag) {
        Optional<Tag> tags = tagRepository.findByName(tag);
        if(tags.isPresent())
        return postRepository.findAll().stream()
                .filter(p -> p.getTags().contains(tags.get()))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
        else return postRepository.findAll().stream()
                .filter(p -> p.getTags().contains(""))
                .sorted(Comparator.comparing(Post::getCreatedDate))
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
            addTagsToPost(post, postDto);
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

    private void addTagsToPost(Post post, PostDto postDto) {
        Set<Tag> tags=new HashSet<>();
        for(String tagName : postDto.getTags()){
            Optional<Tag> tag = tagRepository.findByName(tagName.toLowerCase());
            if(tag.isPresent()) tags.add(tag.get());
            else{
                Tag newTag = new Tag();
                newTag.setName(tagName.toLowerCase());
                newTag.setPosts(Set.of(post));
                beanValidator.validate(newTag);
                TagDto savedTag = tagService.add(mapToDto(newTag));
                if(savedTag!=null){
                    Tag newTag2 = new Tag();
                    newTag2.setId(savedTag.getId());
                    newTag2.setName(savedTag.getName().toLowerCase().trim());
                    newTag2.setPosts(Set.of(post));
                    tags.add(newTag2);
                }
            }
        }
        post.setTags(tags);
    }
}
