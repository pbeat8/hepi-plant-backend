package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.dto.TagDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.Tag;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.TagRepository;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hepiplant.backend.util.ConversionUtils.convertToLocalDate;
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
    private TagRepository tagRepository;
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
    private static Tag tag;
    private static Set<Tag> tags;
    private static TagDto tagDto;

    @BeforeAll
    public static void initializeVariables(){
        user = new User(1L, "username1", "uId1", "email@gmail.com",
                true, "00:00:00", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        category = new Category(2L, "Category1", new ArrayList<>());
        tag = new Tag(1L,"tag1",new HashSet<>(),new HashSet<>());
        tags = new HashSet<>();
        tags.add(tag);
        tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
    }

    @BeforeEach
    public void initializePost(){
        post = new Post(1L, "Post 1", "Body 1", "photo", user, category, tags);
        dto = new PostDto();
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        dto.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setUserId(user.getId());
        dto.setCategoryId(category.getId());
    }

    // CREATE tests

    @Test
    public void shouldCreatePostOk(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));
        given(postRepository.save(postArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PostDto result = postService.create(dto);

        //then
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(times(1)).findByName(eq((String) dto.getTags().toArray()[0]));
        then(beanValidator).should(times(1)).validate(any());
        then(postRepository).should(times(1)).save(any(Post.class));
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getBody());
        assertEquals(1, result.getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.getTags());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(category.getId(), result.getCategoryId());

        Post captorValue = postArgumentCaptor.getValue();
        assertEquals(post.getTitle(), captorValue.getTitle());
        assertEquals(post.getBody(), captorValue.getBody());
        assertEquals(post.getTags(), captorValue.getTags());
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
        List<PostDto> result = postService.getAll(null, null);

        //then
        then(postRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsEmptyListOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of());

        //when
        List<PostDto> result = postService.getAll(null, null);

        //then
        then(postRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    // GET ALL BY DATE tests

    @Test
    public void shouldGetAllPostsByDateOk() throws ParseException {
        //given
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date startDate = formatter.parse("01-01-2021");
        Date endDate = formatter.parse("30-03-2021");
        given(postRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate)))
                .willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAll(startDate, endDate);

        //then
        then(postRepository).should(times(1)).findAllByCreatedDateBetween(any(), any());
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsByDateEmptyListOk() throws ParseException {
        //given
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date startDate = formatter.parse("01-01-2021");
        Date endDate = formatter.parse("30-03-2021");
        given(postRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate)))
                .willReturn(List.of());

        //when
        List<PostDto> result = postService.getAllByFilters(startDate, endDate, null, null);

        //then
        then(postRepository).should(times(1)).findAllByCreatedDateBetween(any(), any());
        assertEquals(0, result.size());
    }

    // GET ALL BY TAG tests

    @Test
    public void shouldGetAllPostsByTagOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of(post));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));

        //when
        List<PostDto> result = postService.getAllByTag((String) post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()).toArray()[0]);

        //then
        then(postRepository).should(times(1)).findAll();
        then(tagRepository).should(times(1)).findByName((String) dto.getTags().toArray()[0]);
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsByTagEmptyListOk(){
        //given
        given(postRepository.findAll()).willReturn(List.of(post));
        given(tagRepository.findByName(anyString())).willReturn(Optional.empty());

        //when
        List<PostDto> result = postService.getAllByTag("someTag");

        //then
        then(postRepository).should(times(1)).findAll();
        then(tagRepository).should(times(1)).findByName(anyString());
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllPostsByUserOk(){
        //given
        given(userRepository.findById(post.getUser().getId())).willReturn(Optional.of(user));
        given(postRepository.findAllByUser(post.getUser())).willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAllByUser(post.getUser().getId());

        //then
        then(userRepository).should(times(1)).findById(dto.getUserId());
        then(postRepository).should(times(1)).findAllByUser(any(User.class));
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsByUserEmptyListOk(){
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(postRepository.findAllByUser(post.getUser())).willReturn(List.of());

        //when
        List<PostDto> result = postService.getAllByUser(anyLong());

        //then
        then(userRepository).should(times(1)).findById(anyLong());
        then(postRepository).should(times(1)).findAllByUser(any(User.class));
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllPostsByUserIdUserDoesNotExistThrowsException(){
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.getAllByUser(anyLong()));
        then(userRepository).should(times(1)).findById(anyLong());
        then(postRepository).should(times(0)).findAllByUser(any(User.class));
    }

    @Test
    public void shouldGetAllPostsByCategoryOk(){
        //given
        given(categoryRepository.findById(post.getCategory().getId())).willReturn(Optional.of(category));
        given(postRepository.findAllByCategory(post.getCategory())).willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAllByCategory(post.getCategory().getId());

        //then
        then(categoryRepository).should(times(1)).findById(dto.getCategoryId());
        then(postRepository).should(times(1)).findAllByCategory(any(Category.class));
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(post.getBody(), result.get(0).getBody());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllPostsByCategoryEmptyListOk(){
        //given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(postRepository.findAllByCategory(post.getCategory())).willReturn(List.of());

        //when
        List<PostDto> result = postService.getAllByCategory(anyLong());

        //then
        then(categoryRepository).should(times(1)).findById(anyLong());
        then(postRepository).should(times(1)).findAllByCategory(any(Category.class));
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllPostsByCategoryIdDoesNotExistThrowsException(){
        //given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> postService.getAllByCategory(anyLong()));
        then(categoryRepository).should(times(1)).findById(anyLong());
        then(postRepository).should(times(0)).findAllByCategory(any(Category.class));
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
        assertEquals(1, result.getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.getTags());
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
        postToUpdate.setTags(new HashSet<>());
        dto.setUserId(null);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName(anyString())).willReturn(Optional.of(tag));
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
        assertEquals(1, result.getTags().size());
        assertEquals(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.getTags());
        assertEquals(category.getId(), result.getCategoryId());

        Post captorValue = postArgumentCaptor.getValue();
        assertEquals(post.getTitle(), captorValue.getTitle());
        assertEquals(post.getBody(), captorValue.getBody());
        assertEquals(post.getTags(), captorValue.getTags());
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
        postToUpdate.setTags(new HashSet<>());
        dto.setUserId(null);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> postService.update(post.getId(), dto));
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
//        then(tagRepository).should(atMostOnce()).save(tag);
        then(postRepository).should(times(0)).save(any(Post.class));
    }

    @Test
    public void shouldUpdatePostUserChangeThrowsException(){
        //given
        Post postToUpdate = new Post();
        postToUpdate.setTags(new HashSet<>());

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
        postToUpdate.setTags(new HashSet<>());
        dto.setUserId(null);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(postToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> postService.update(post.getId(), dto));
        then(postRepository).should(times(1)).findById(post.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(atMostOnce()).findByName(eq((String)dto.getTags().toArray()[0]));
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
