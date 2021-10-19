package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.CommentDto;
import com.hepiplant.backend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private static final String POSTS_URL = "/posts/{postId}";
    private static final String SALES_OFFERS_URL = "/salesoffers/{salesOfferId}";

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping(value = POSTS_URL + "/comments")
    public ResponseEntity<CommentDto> addPostComment(@PathVariable Long postId, @RequestBody CommentDto commentDto){
        commentDto.setPostId(postId);
        return ResponseEntity.ok().body(commentService.createPostComment(commentDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping(value = SALES_OFFERS_URL + "/comments")
    public ResponseEntity<CommentDto> addSalesOfferComment(@PathVariable Long salesOfferId, @RequestBody CommentDto commentDto){
        commentDto.setPostId(salesOfferId);
        return ResponseEntity.ok().body(commentService.createSalesOfferComment(commentDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = POSTS_URL + "/comments")
    public ResponseEntity<List<CommentDto>> getPostComments(@PathVariable Long postId){
        return ResponseEntity.ok().body(commentService.getAllByPost(postId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = SALES_OFFERS_URL + "/comments")
    public ResponseEntity<List<CommentDto>> getSalesOfferComments(@PathVariable Long salesOfferId){
        return ResponseEntity.ok().body(commentService.getAllBySalesOffer(salesOfferId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping(value = POSTS_URL + "/comments/{commentId}")
    public ResponseEntity<CommentDto> updatePostComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                        @RequestBody CommentDto commentDto){
        return ResponseEntity.ok().body(commentService.updatePostComment(commentId, commentDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping(value = SALES_OFFERS_URL + "/comments/{commentId}")
    public ResponseEntity<CommentDto> updateSalesOfferComment(@PathVariable Long salesOfferId, @PathVariable Long commentId,
                                                              @RequestBody CommentDto commentDto){
        return ResponseEntity.ok().body(commentService.updateSalesOfferComment(commentId, commentDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping(value = POSTS_URL + "/comments/{commentId}")
    public ResponseEntity<String> deletePostComment(@PathVariable Long postId, @PathVariable Long commentId){
        return ResponseEntity.ok().body(commentService.deletePostComment(commentId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping(value = SALES_OFFERS_URL + "/comments/{commentId}")
    public ResponseEntity<String> deleteSalesOfferComment(@PathVariable Long salesOfferId, @PathVariable Long commentId){
        return ResponseEntity.ok().body(commentService.deleteSalesOfferComment(commentId));
    }
}
