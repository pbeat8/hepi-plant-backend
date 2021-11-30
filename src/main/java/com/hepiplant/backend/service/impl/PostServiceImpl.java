package com.hepiplant.backend.service.impl;


import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.Tag;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.TagRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.PostService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;
import static com.hepiplant.backend.util.ConversionUtils.convertToLocalDate;
import static java.util.Optional.ofNullable;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final BeanValidator beanValidator;

    public PostServiceImpl(final PostRepository postRepository,
                           final CategoryRepository categoryRepository,
                           final UserRepository userRepository,
                           final TagRepository tagRepository,
                           final BeanValidator beanValidator) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.beanValidator = beanValidator;
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
        ofNullable(postDto.getTags()).ifPresent(c -> addTagsToPost(post, postDto));
        beanValidator.validate(post);
        Post savedPost = postRepository.save(post);
        return mapToDto(savedPost);
    }

    @Override
    public List<PostDto> getAll(Date startDate, Date endDate) {
            return postRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate).plusDays(1)).stream()
                    .sorted(Comparator.comparing(Post::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
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
    public List<PostDto> getAllByFilters(Date startDate, Date endDate, String tag, Long categoryId) {
        final Set<PostDto>[] posts = new Set[]{new HashSet<>()};
        final boolean[] wasInIf = {false};
        if(startDate==null && endDate==null && tag==null && categoryId==null){
            posts[0].addAll(postRepository.findAll().stream()
                    .sorted(Comparator.comparing(Post::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList()));
            wasInIf[0] =true;
        }
        if(startDate!=null && endDate!=null){
            List<PostDto> temp= getAll(startDate,endDate);
            posts[0] = getPostDtos(posts[0], temp, wasInIf[0]);
            wasInIf[0] =true;
        }

        if(tag!=null && !tag.isEmpty()){
            List<PostDto> temp= getAllByTag(tag);
            posts[0] = getPostDtos(posts[0], temp, wasInIf[0]);
            wasInIf[0] =true;
        }

        ofNullable(categoryId).ifPresent(c -> {
            List<PostDto> temp = getAllByCategory(categoryId);
            posts[0] = getPostDtos(posts[0], temp, wasInIf[0]);
            wasInIf[0] =true;
        });

        List<PostDto> filterPost = new ArrayList<>(posts[0]);
        return filterPost.stream()
                .sorted(Comparator.comparing(PostDto::getCreatedDate).reversed())
                .collect(Collectors.toList());
    }

    private Set<PostDto> getPostDtos(Set<PostDto> posts, List<PostDto> temp, boolean was) {
        if (!posts.isEmpty()) {
            Set<Long> indexes = temp.stream().map(PostDto::getId).collect(Collectors.toSet());
            Set<PostDto> newPosts = posts.stream().filter(p -> indexes.contains(p.getId())).collect(Collectors.toSet());
            posts = newPosts;
        }
        else if(was && posts.isEmpty()){
            posts = new HashSet<>();
        }
        else posts.addAll(temp);
        return posts;
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
        ofNullable(postDto.getTitle()).ifPresent(c -> post.setTitle(postDto.getTitle()));
        ofNullable(postDto.getBody()).ifPresent(c -> post.setBody(postDto.getBody()));
        if(postDto.getTags() != null && !postDto.getTags().isEmpty()){
            Set<Tag> oldTags = post.getTags();
            addTagsToPost(post, postDto);
            oldTags = oldTags.stream()
                    .filter(t -> !postDto.getTags().contains(t.getName()))
                    .collect(Collectors.toSet());
            removeOrphanTags(oldTags);
        }
        ofNullable(postDto.getPhoto()).ifPresent(c -> post.setPhoto(postDto.getPhoto()));
        ofNullable(postDto.getUserId()).ifPresent(c -> {throw new ImmutableFieldException("Cannot change User for Post!");});
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
        Post postValue = post.get();
        removeOrphanTags(postValue.getTags());
        postRepository.delete(postValue);
        return "Successfully deleted the post with id = " + id;
    }

    private void addTagsToPost(Post post, PostDto postDto) {
        Set<Tag> tags=new HashSet<>();
        for(String tagName : postDto.getTags()){
            Optional<Tag> tag = tagRepository.findByName(tagName.toLowerCase());
            if(tag.isPresent()) tags.add(tag.get());
            else {
                Tag newTag = new Tag();
                newTag.setName(tagName.toLowerCase());
                beanValidator.validate(newTag);
                Tag savedTag = tagRepository.save(newTag);
                tags.add(savedTag);
            }
        }
        post.setTags(tags);
    }

    private void removeOrphanTags(Set<Tag> tags) {
        tags.forEach(tag -> {
            long relatedItemsCount = tagRepository.countPostsAndSalesOffersByTagId(tag.getId());
            if (relatedItemsCount < 2) {
                tagRepository.delete(tag);
            }
        });
    }
}
