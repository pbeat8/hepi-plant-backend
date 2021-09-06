package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.entity.enums.Permission;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<Post> postArgumentCaptor;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDto dto;
    private static User user;
    private static Category category;

    @BeforeAll
    public static void initializeVariables(){
        user = new User(1L, "username1", "login1", "p@ssw0rd", "email@gmail.com",
                Permission.USER, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        category = new Category(2L, "Category1", new ArrayList<>());
    }

    @BeforeEach
    public void initializePost(){
        post = new Post(1L, "Post 1", "Body 1", "tag1", "tag2", "tag3", null, null, user, category);
        dto = new PostDto();
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        dto.setTags(List.of(post.getTag1(), post.getTag2(), post.getTag3()));
        dto.setUserId(user.getId());
        dto.setCategoryId(category.getId());
    }

    // CREATE tests

    @Test
    public void shouldCreatePostOk(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(postRepository.save(postArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PostDto result = postService.create(dto);

        //then
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(postRepository).should(times(1)).save(any(Post.class));
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getBody());
        assertEquals(3, result.getTags().size());
        assertEquals(post.getTag1(), result.getTags().get(0));
        assertEquals(post.getTag2(), result.getTags().get(1));
        assertEquals(post.getTag3(), result.getTags().get(2));
        assertEquals(user.getId(), result.getUserId());
        assertEquals(category.getId(), result.getCategoryId());

        Post captorValue = postArgumentCaptor.getValue();
        assertEquals(post.getTitle(), captorValue.getTitle());
        assertEquals(post.getBody(), captorValue.getBody());
        assertEquals(post.getTag1(), captorValue.getTag1());
        assertEquals(post.getTag2(), captorValue.getTag2());
        assertEquals(post.getTag3(), captorValue.getTag3());
        assertEquals(post.getTag4(), captorValue.getTag4());
        assertEquals(post.getTag5(), captorValue.getTag5());
        assertEquals(user, captorValue.getUser());
        assertEquals(category, captorValue.getCategory());
    }

    @Test
    public void shouldCreatePostUserDoesNotExistThrowsException(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    @Test
    public void shouldCreatePostCategoryDoesNotExistThrowsException(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    @Test
    public void shouldCreatePostInvalidValuesThrowsException(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> postService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    // GET ALL tests

    @Test
    public void shouldGetAllPostsOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAll();

        //then
        then(postRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(3, result.get(0).getTags().size());
        assertEquals(post.getTag1(), result.get(0).getTags().get(0));
        assertEquals(post.getTag2(), result.get(0).getTags().get(1));
        assertEquals(post.getTag3(), result.get(0).getTags().get(2));
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsEmptyListOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of());

        //when
        List<PostDto> result = postService.getAll();

        //then
        then(postRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    // GET ALL BY TAG tests

    @Test
    public void shouldGetAllPostsByTagOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAllByTag(post.getTag1());

        //then
        then(postRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(3, result.get(0).getTags().size());
        assertEquals(post.getTag1(), result.get(0).getTags().get(0));
        assertEquals(post.getTag2(), result.get(0).getTags().get(1));
        assertEquals(post.getTag3(), result.get(0).getTags().get(2));
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsByTagEmptyListOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAllByTag("someTag");

        //then
        then(postRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    // GET BY ID tests

    @Test
    public void shouldGetPostByIdOk(){
        //given
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        //when
        PostDto result = postService.getById(post.getId());

        //then
        then(postRepository).should(times(1)).findById(post.getId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getBody());
        assertEquals(3, result.getTags().size());
        assertEquals(post.getTag1(), result.getTags().get(0));
        assertEquals(post.getTag2(), result.getTags().get(1));
        assertEquals(post.getTag3(), result.getTags().get(2));
        assertEquals(user.getId(), result.getUserId());
        assertEquals(category.getId(), result.getCategoryId());
    }

    @Test
    public void shouldGetPostByIdPostDoesNotExistThrowsException(){
        //given
        given(postRepository.findById(post.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.getById(post.getId()));
        then(postRepository).should(times(1)).findById(post.getId());
    }

    // UPDATE tests

    @Test
    public void shouldUpdatePostOk(){
        //given
        Post postToUpdate = new Post();
        dto.setUserId(null);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(postRepository.save(postArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PostDto result = postService.update(post.getId(), dto);

        //then
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(postRepository).should(times(1)).save(any(Post.class));
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getBody());
        assertEquals(3, result.getTags().size());
        assertEquals(post.getTag1(), result.getTags().get(0));
        assertEquals(post.getTag2(), result.getTags().get(1));
        assertEquals(post.getTag3(), result.getTags().get(2));
        assertEquals(category.getId(), result.getCategoryId());

        Post captorValue = postArgumentCaptor.getValue();
        assertEquals(post.getTitle(), captorValue.getTitle());
        assertEquals(post.getBody(), captorValue.getBody());
        assertEquals(post.getTag1(), captorValue.getTag1());
        assertEquals(post.getTag2(), captorValue.getTag2());
        assertEquals(post.getTag3(), captorValue.getTag3());
        assertEquals(post.getTag4(), captorValue.getTag4());
        assertEquals(post.getTag5(), captorValue.getTag5());
        assertEquals(category, captorValue.getCategory());
    }

    @Test
    public void shouldUpdatePostDoesNotExistThrowsException(){
        //given
        dto.setUserId(null);

        given(postRepository.findById(post.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.update(post.getId(), dto));
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    @Test
    public void shouldUpdatePostCategoryDoesNotExistThrowsException(){
        //given
        Post postToUpdate = new Post();
        dto.setUserId(null);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.update(post.getId(), dto));
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    @Test
    public void shouldUpdatePostUserChangeThrowsException(){
        //given
        Post postToUpdate = new Post();

        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));

        //when

        //then
        assertThrows(ImmutableFieldException.class, () -> postService.update(post.getId(), dto));
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    @Test
    public void shouldUpdatePostInvalidValuesThrowsException(){
        //given
        Post postToUpdate = new Post();
        dto.setUserId(null);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> postService.update(post.getId(), dto));
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    // DELETE tests
    @Test
    public void shouldDeletePostOk(){
        //given
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        //when
        String result = postService.delete(post.getId());

        //then
        then(postRepository).should(times(1)).findById(post.getId());
        then(postRepository).should(times(1)).delete(post);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeletePostDoesNotExistThrowsException(){
        //given
        given(postRepository.findById(post.getId())).willReturn(Optional.empty());

        //when
        String result = postService.delete(post.getId());

        //then
        then(postRepository).should(times(1)).findById(post.getId());
        then(postRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No post"));
    }

}
