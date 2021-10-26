package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CommentDto;
import com.hepiplant.backend.dto.SalesOfferDto;
import com.hepiplant.backend.entity.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;


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
                null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        post = new Post(1L,"postTitle","postBody","photo",user, category,new HashSet<>());
        salesOffer = new SalesOffer(1L,"salesOfferTitle","salesOfferBody","location",new BigDecimal(10.00),"photo",user, category,new HashSet<>());
    }

    @BeforeEach
    public void initializeSalesOffer(){
        postComment = new PostComment(1L,"body", LocalDateTime.now(),LocalDateTime.now(),post,user);
        salesOfferComment = new SalesOfferComment(1L,"body",LocalDateTime.now(),LocalDateTime.now(),salesOffer,user);
        dto = new CommentDto();
        dto.setBody(salesOfferComment.getBody());
        dto.setCreatedDate(salesOfferComment.getCreatedDate());
        dto.setUpdatedDate(salesOfferComment.getUpdatedDate());
        dto.setPostId(salesOfferComment.getId());
        dto.setUsername(user.getUsername());
        dto.setId(1L);
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
        assertEquals(postComment.getId(),result.getId());
        assertEquals(postComment.getBody(),result.getBody());
        assertEquals(postComment.getCreatedDate(),result.getCreatedDate());
        assertEquals(postComment.getUpdatedDate(),result.getUpdatedDate());
        assertEquals(postComment.getUser().getId(),result.getUserId());
        assertEquals(postComment.getUser().getUsername(),result.getUsername());
        assertEquals(postComment.getPost().getId(),result.getPostId());
    }
}
