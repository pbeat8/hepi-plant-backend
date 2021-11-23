package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CommentDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.*;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostCommentRepository postCommentRepository;
    @Mock
    private SalesOfferRepository salesOfferRepository;
    @Mock
    private SalesOfferCommentRepository salesOfferCommentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<PostComment> postCommentArgumentCaptor;
    @Captor
    private ArgumentCaptor<SalesOfferComment> salesOfferCommentArgumentCaptor;

    @InjectMocks
    private CommentServiceImpl commentService;

    private static PostComment postComment;
    private static SalesOfferComment salesOfferComment;
    private static CommentDto dto;
    private static User user;
    private static Post post;
    private static SalesOffer salesOffer;
    private static Category category;


    @BeforeAll
    public static void initializeVariables(){
        category = new Category(2L, "Category1", new ArrayList<>());
        user = new User(1L, "username1", "uId1", "email@gmail.com",
                true, "00:00:00", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @BeforeEach
    public void initializeSalesOffer(){
        post = new Post(1L,"postTitle","postBody","photo",user, category,new HashSet<>());
        post.setComments(new ArrayList<PostComment>(
                Arrays.asList(new PostComment(1L,"body", LocalDateTime.now(),LocalDateTime.now(),post,user))));
        salesOffer = new SalesOffer(1L,"salesOfferTitle","salesOfferBody","location",new BigDecimal(10.00),"photo",user, category,new HashSet<>());
        salesOffer.setComments(new ArrayList<SalesOfferComment>(
                Arrays.asList(new SalesOfferComment(1L,"body", LocalDateTime.now(),LocalDateTime.now(),salesOffer,user))));
        postComment = new PostComment(1L,"body", LocalDateTime.now(),LocalDateTime.now(),post,user);
        salesOfferComment = new SalesOfferComment(1L,"body",LocalDateTime.now(),LocalDateTime.now(),salesOffer,user);
        dto = new CommentDto();
        dto.setBody(postComment.getBody());
        dto.setCreatedDate(postComment.getCreatedDate());
        dto.setUpdatedDate(postComment.getUpdatedDate());
        dto.setPostId(postComment.getId());
        dto.setUsername(user.getUsername());
        dto.setId(postComment.getId());
        dto.setUserId(user.getId());
    }

    @Test
    public void shouldCretePostCommentOk(){
        //given
        given(postRepository.findById(dto.getPostId())).willReturn(Optional.of(post));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(postCommentRepository.save(postCommentArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        CommentDto result = commentService.createPostComment(dto);

        //then
        then(postRepository).should(times(1)).findById(dto.getPostId());
        then(userRepository).should(times(1)).findById(dto.getUserId());
        then(beanValidator).should(times(1)).validate(any());
        then(postCommentRepository).should(times(1)).save(any(PostComment.class));
        assertEquals(postComment.getBody(),result.getBody());
        assertEquals(postComment.getUser().getId(),result.getUserId());
        assertEquals(postComment.getUser().getUsername(),result.getUsername());
        assertEquals(postComment.getPost().getId(),result.getPostId());
    }

    @Test
    public void shouldCreteSalesOfferCommentOk(){
        //given
        given(salesOfferRepository.findById(dto.getPostId())).willReturn(Optional.of(salesOffer));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(salesOfferCommentRepository.save(salesOfferCommentArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        CommentDto result = commentService.createSalesOfferComment(dto);

        //then
        then(salesOfferRepository).should(times(1)).findById(dto.getPostId());
        then(userRepository).should(times(1)).findById(dto.getUserId());
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferCommentRepository).should(times(1)).save(any(SalesOfferComment.class));
        assertEquals(salesOfferComment.getBody(),result.getBody());
        assertEquals(salesOfferComment.getUser().getId(),result.getUserId());
        assertEquals(salesOfferComment.getUser().getUsername(),result.getUsername());
        assertEquals(salesOfferComment.getSalesOffer().getId(),result.getPostId());
    }

    @Test
    public void shouldCreatePostCommentUserDoesNotExistThrowsException(){
        //given
        given(postRepository.findById(dto.getPostId())).willReturn(Optional.of(post));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> commentService.createPostComment(dto));
        then(postRepository).should(times(1)).findById(dto.getPostId());
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(postCommentRepository).should(times(0)).save(any(PostComment.class));
    }

    @Test
    public void shouldCreateSalesOfferCommentUserDoesNotExistThrowsException(){
        //given
        given(salesOfferRepository.findById(dto.getPostId())).willReturn(Optional.of(salesOffer));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> commentService.createSalesOfferComment(dto));
        then(salesOfferRepository).should(times(1)).findById(dto.getPostId());
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(salesOfferCommentRepository).should(times(0)).save(any(SalesOfferComment.class));
    }

    @Test
    public void shouldCreatePostCommentInvalidValuesThrowsException(){
        //given
        given(postRepository.findById(dto.getPostId())).willReturn(Optional.of(post));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> commentService.createPostComment(dto));
        then(postRepository).should(times(1)).findById(dto.getPostId());
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(beanValidator).should(times(1)).validate(any());
        then(postCommentRepository).should(times(0)).save(any(PostComment.class));
    }

    @Test
    public void shouldCreateSalesOfferCommentInvalidValuesThrowsException(){
        //given
        given(salesOfferRepository.findById(dto.getPostId())).willReturn(Optional.of(salesOffer));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> commentService.createSalesOfferComment(dto));
        then(salesOfferRepository).should(times(1)).findById(dto.getPostId());
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferCommentRepository).should(times(0)).save(any(SalesOfferComment.class));
    }

    @Test
    public void shouldGetAllCommentsByPostOk(){
        //given
        given(postRepository.findById(dto.getPostId())).willReturn(Optional.of(post));

        //when
        List<CommentDto> result = commentService.getAllByPost(dto.getPostId());

        //then
        then(postRepository).should(times(1)).findById(dto.getPostId());
        assertEquals(1,result.size());
        assertEquals(postComment.getBody(),result.get(0).getBody());
        assertEquals(postComment.getUser().getId(),result.get(0).getUserId());
        assertEquals(postComment.getUser().getUsername(),result.get(0).getUsername());
        assertEquals(postComment.getPost().getId(),result.get(0).getPostId());

    }

    @Test
    public void shouldGetAllCommentsBySalesOfferOk(){
        //given
        given(salesOfferRepository.findById(dto.getPostId())).willReturn(Optional.of(salesOffer));

        //when
        List<CommentDto> result = commentService.getAllBySalesOffer(dto.getPostId());

        //then
        then(salesOfferRepository).should(times(1)).findById(dto.getPostId());
        assertEquals(1,result.size());
        assertEquals(salesOfferComment.getBody(),result.get(0).getBody());
        assertEquals(salesOfferComment.getUser().getId(),result.get(0).getUserId());
        assertEquals(salesOfferComment.getUser().getUsername(),result.get(0).getUsername());
        assertEquals(salesOfferComment.getSalesOffer().getId(),result.get(0).getPostId());

    }

    @Test
    public void shouldGetAllCommentsByPostEmptyListOk(){
        //given
        post.setComments(new ArrayList<>());
        given(postRepository.findById(dto.getPostId())).willReturn(Optional.of(post));

        //when
        List<CommentDto> result = commentService.getAllByPost(dto.getId());

        //then
        then(postRepository).should(times(1)).findById(dto.getPostId());
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllCommentsBySalesOfferEmptyListOk(){
        //given
        salesOffer.setComments(new ArrayList<>());
        given(salesOfferRepository.findById(dto.getPostId())).willReturn(Optional.of(salesOffer));

        //when
        List<CommentDto> result = commentService.getAllBySalesOffer(dto.getId());

        //then
        then(salesOfferRepository).should(times(1)).findById(dto.getPostId());
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllCommentsByPostIdPostDoesNotExistThrowsException(){
        //given
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> commentService.getAllByPost(anyLong()));
        then(postRepository).should(times(1)).findById(anyLong());
    }

    @Test
    public void shouldGetAllCommentsBySalesOfferIdSalesOfferDoesNotExistThrowsException(){
        //given
        given(salesOfferRepository.findById(anyLong())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> commentService.getAllBySalesOffer(anyLong()));
        then(salesOfferRepository).should(times(1)).findById(anyLong());
    }

    @Test
    public void shouldUpdatePostCommentOk(){
        //given
        PostComment postCommentToUpdate = new PostComment();
        dto.setUserId(null);
        dto.setPostId(null);
        given(postCommentRepository.findById(dto.getId())).willReturn(Optional.of(postCommentToUpdate));
        given(postCommentRepository.save(postCommentArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        CommentDto result = commentService.updatePostComment(postComment.getId(),dto);

        //then
        then(postCommentRepository).should(times(1)).findById(postComment.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(postCommentRepository).should(times(1)).save(any(PostComment.class));
        assertEquals(postComment.getBody(),result.getBody());

        PostComment captureValue = postCommentArgumentCaptor.getValue();
        assertEquals(postComment.getBody(),captureValue.getBody());

    }

    @Test
    public void shouldUpdateSalesOfferCommentOk(){
        //given
        SalesOfferComment salesOfferCommentToUpdate = new SalesOfferComment();
        dto.setUserId(null);
        dto.setPostId(null);
        given(salesOfferCommentRepository.findById(dto.getId())).willReturn(Optional.of(salesOfferCommentToUpdate));
        given(salesOfferCommentRepository.save(salesOfferCommentArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        CommentDto result = commentService.updateSalesOfferComment(salesOfferComment.getId(),dto);

        //then
        then(salesOfferCommentRepository).should(times(1)).findById(dto.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferCommentRepository).should(times(1)).save(any(SalesOfferComment.class));
        assertEquals(salesOfferComment.getBody(),result.getBody());

        SalesOfferComment captureValue = salesOfferCommentArgumentCaptor.getValue();
        assertEquals(salesOfferComment.getBody(),captureValue.getBody());

    }

    @Test
    public void shouldUpdatePostCommentDoesNotExistThrowsException(){
        //given
        dto.setUserId(null);
        dto.setPostId(null);

        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> commentService.updatePostComment(postComment.getId(), dto));
        then(postCommentRepository).should(times(1)).findById(postComment.getId());
        then(postCommentRepository).should(times(0)).save(any(PostComment.class));
    }

    @Test
    public void shouldUpdateSalesOfferCommentDoesNotExistThrowsException(){
        //given
        dto.setUserId(null);
        dto.setPostId(null);

        given(salesOfferCommentRepository.findById(salesOfferComment.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> commentService.updateSalesOfferComment(salesOfferComment.getId(), dto));
        then(salesOfferCommentRepository).should(times(1)).findById(salesOfferComment.getId());
        then(salesOfferCommentRepository).should(times(0)).save(any(SalesOfferComment.class));
    }

    @Test
    public void shouldUpdatePostCommentUserChangeThrowsException(){
        //given
        PostComment postCommentToUpdate = new PostComment();

        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.of(postCommentToUpdate));

        //when

        //then
        assertThrows(ImmutableFieldException.class, () -> commentService.updatePostComment(postComment.getId(), dto));
        then(postCommentRepository).should(atMostOnce()).findById(postComment.getId());
        then(postCommentRepository).should(times(0)).save(any(PostComment.class));
    }

    @Test
    public void shouldUpdateSalesOfferCommentUserChangeThrowsException(){
        //given
        SalesOfferComment salesOfferCommentToUpdate = new SalesOfferComment();

        given(salesOfferCommentRepository.findById(salesOfferComment.getId())).willReturn(Optional.of(salesOfferCommentToUpdate));

        //when

        //then
        assertThrows(ImmutableFieldException.class, () -> commentService.updateSalesOfferComment(salesOfferComment.getId(), dto));
        then(salesOfferCommentRepository).should(atMostOnce()).findById(salesOfferComment.getId());
        then(salesOfferCommentRepository).should(times(0)).save(any(SalesOfferComment.class));
    }

    @Test
    public void shouldUpdatePostCommentInvalidValuesThrowsException() {
        //given
        PostComment postCommentToUpdate = new PostComment();
        dto.setUserId(null);
        dto.setPostId(null);
        given(postCommentRepository.findById(dto.getId())).willReturn(Optional.of(postCommentToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> commentService.updatePostComment(postComment.getId(), dto));
        then(postCommentRepository).should(times(1)).findById(postComment.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(postCommentRepository).should(times(0)).save(any(PostComment.class));
    }

    @Test
    public void shouldUpdateSalesOfferCommentInvalidValuesThrowsException() {
        //given
        SalesOfferComment salesOfferCommentToUpdate = new SalesOfferComment();
        dto.setUserId(null);
        dto.setPostId(null);
        given(salesOfferCommentRepository.findById(dto.getId())).willReturn(Optional.of(salesOfferCommentToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> commentService.updateSalesOfferComment(salesOfferComment.getId(), dto));
        then(salesOfferCommentRepository).should(times(1)).findById(salesOfferComment.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferCommentRepository).should(times(0)).save(any(SalesOfferComment.class));
    }

    @Test
    public void shouldDeletePostCommentOk(){
        //given
        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.of(postComment));

        //when
        String result = commentService.deletePostComment(postComment.getId());

        //then
        then(postCommentRepository).should(times(1)).findById(postComment.getId());
        then(postCommentRepository).should(times(1)).delete(postComment);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeleteSalesOfferCommentOk(){
        //given
        given(salesOfferCommentRepository.findById(salesOfferComment.getId())).willReturn(Optional.of(salesOfferComment));

        //when
        String result = commentService.deleteSalesOfferComment(salesOfferComment.getId());

        //then
        then(salesOfferCommentRepository).should(times(1)).findById(salesOfferComment.getId());
        then(salesOfferCommentRepository).should(times(1)).delete(salesOfferComment);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeletePostCommentDoesNotExistThrowsException(){
        //given
        given(postCommentRepository.findById(postComment.getId())).willReturn(Optional.empty());

        //when
        String result = commentService.deletePostComment(postComment.getId());

        //then
        then(postCommentRepository).should(times(1)).findById(postComment.getId());
        then(postCommentRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No post"));
    }

    @Test
    public void shouldDeleteSalesOfferCommentDoesNotExistThrowsException(){
        //given
        given(salesOfferCommentRepository.findById(salesOfferComment.getId())).willReturn(Optional.empty());

        //when
        String result = commentService.deleteSalesOfferComment(salesOfferComment.getId());

        //then
        then(salesOfferCommentRepository).should(times(1)).findById(salesOfferComment.getId());
        then(salesOfferCommentRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No post"));
    }
}
