package com.leesh.devlab.controller;

import com.leesh.devlab.config.LoginMemberAnno;
import com.leesh.devlab.constant.dto.CommentDetailDto;
import com.leesh.devlab.constant.dto.LikeResponseDto;
import com.leesh.devlab.constant.dto.LoginMemberDto;
import com.leesh.devlab.service.CommentService;
import com.leesh.devlab.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping(value = "/{comment-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDetailDto> getDetails(@PathVariable("comment-id") Long commentId) {

        return ResponseEntity.ok(commentService.getDetails(commentId));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDetailDto>> getLists(Pageable pageable) {

        return ResponseEntity.ok(commentService.getLists(pageable));
    }

    @DeleteMapping(value = "/{comment-id}")
    public ResponseEntity<Void> delete(@PathVariable("comment-id") Long commentId, @LoginMemberAnno LoginMemberDto loginMemberDto) {

        commentService.delete(commentId, loginMemberDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{comment-id}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LikeResponseDto> createLike(@LoginMemberAnno LoginMemberDto loginMemberDto, @PathVariable("comment-id") Long commentId) {

        LikeResponseDto likeResponseDto = likeService.createCommentLike(loginMemberDto, commentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDto);
    }

}
