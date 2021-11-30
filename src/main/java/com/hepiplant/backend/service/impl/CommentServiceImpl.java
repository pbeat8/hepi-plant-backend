package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CommentDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.*;
import com.hepiplant.backend.service.CommentService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;
import static java.util.Optional.ofNullable;

@Service
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final SalesOfferRepository salesOfferRepository;
    private final SalesOfferCommentRepository salesOfferCommentRepository;
    private final UserRepository userRepository;
    private final BeanValidator beanValidator;

    public CommentServiceImpl(PostRepository postRepository,
                              PostCommentRepository postCommentRepository,
                              SalesOfferRepository salesOfferRepository,
                              SalesOfferCommentRepository salesOfferCommentRepository,
                              UserRepository userRepository,
                              BeanValidator beanValidator) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.salesOfferRepository = salesOfferRepository;
        this.salesOfferCommentRepository = salesOfferCommentRepository;
        this.userRepository = userRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public CommentDto createPostComment(CommentDto commentDto) {
        PostComment comment = new PostComment();
        comment.setBody(commentDto.getBody());
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id " + commentDto.getPostId()));
        comment.setPost(post);
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for id " + commentDto.getUserId()));
        comment.setUser(user);
        beanValidator.validate(comment);
        PostComment savedComment = postCommentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    public CommentDto createSalesOfferComment(CommentDto commentDto) {
        SalesOfferComment comment = new SalesOfferComment();
        comment.setBody(commentDto.getBody());
        SalesOffer salesOffer = salesOfferRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Sales offer not found for id " + commentDto.getPostId()));
        comment.setSalesOffer(salesOffer);
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for id " + commentDto.getUserId()));
        comment.setUser(user);
        beanValidator.validate(comment);
        SalesOfferComment savedComment = salesOfferCommentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    public List<CommentDto> getAllByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id " + postId));
        return post.getComments().stream()
                .sorted(Comparator.comparing(PostComment::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllBySalesOffer(Long salesOfferId) {
        SalesOffer salesOffer = salesOfferRepository.findById(salesOfferId)
                .orElseThrow(() -> new EntityNotFoundException("Sales offer not found for id " + salesOfferId));
        return salesOffer.getComments().stream()
                .sorted(Comparator.comparing(SalesOfferComment::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updatePostComment(Long id, CommentDto commentDto) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post comment not found for id " + id));
        ofNullable(commentDto.getBody())
                .ifPresent(c -> comment.setBody(commentDto.getBody()));
        ofNullable(commentDto.getPostId())
                .ifPresent(c -> {throw new ImmutableFieldException("Cannot change Post for Comment!");});
        ofNullable(commentDto.getUserId())
            .ifPresent(c -> {throw new ImmutableFieldException("Cannot change User for Comment!");});

        beanValidator.validate(comment);
        PostComment savedComment = postCommentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    public CommentDto updateSalesOfferComment(Long id, CommentDto commentDto) {
        SalesOfferComment comment = salesOfferCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales offer comment not found for id " + id));
        ofNullable(commentDto.getBody())
                .ifPresent(c -> comment.setBody(commentDto.getBody()));
        ofNullable(commentDto.getPostId())
                .ifPresent(c -> {throw new ImmutableFieldException("Cannot change Sales offer for Comment!");});
        ofNullable(commentDto.getUserId())
                .ifPresent(c -> {throw new ImmutableFieldException("Cannot change User for Comment!");});

        beanValidator.validate(comment);
        SalesOfferComment savedComment = salesOfferCommentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    public String deletePostComment(Long commentId) {
        Optional<PostComment> comment = postCommentRepository.findById(commentId);
        if(comment.isEmpty()){
            return "No post with id = " + commentId;
        }
        postCommentRepository.delete(comment.get());
        return "Successfully deleted the post comment with id = "+ commentId;
    }

    @Override
    public String deleteSalesOfferComment(Long commentId) {
        Optional<SalesOfferComment> comment = salesOfferCommentRepository.findById(commentId);
        if(comment.isEmpty()){
            return "No post with id = " + commentId;
        }
        salesOfferCommentRepository.delete(comment.get());
        return "Successfully deleted the sales offer comment with id = "+ commentId;
    }
}
