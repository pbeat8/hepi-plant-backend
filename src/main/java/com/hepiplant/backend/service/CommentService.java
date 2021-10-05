package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createPostComment(CommentDto commentDto);
    CommentDto createSalesOfferComment(CommentDto commentDto);
    List<CommentDto> getAllByPost(Long postId);
    List<CommentDto> getAllBySalesOffer(Long salesOfferId);
    CommentDto updatePostComment(Long id, CommentDto commentDto);
    CommentDto updateSalesOfferComment(Long id, CommentDto commentDto);
    String deletePostComment(Long commentId);
    String deleteSalesOfferComment(Long commentId);

}
