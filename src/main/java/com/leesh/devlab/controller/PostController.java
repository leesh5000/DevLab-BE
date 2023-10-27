package com.leesh.devlab.controller;

import com.leesh.devlab.config.LoginMemberAnno;
import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.service.CommentService;
import com.leesh.devlab.service.LikeService;
import com.leesh.devlab.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping(value = "/{post-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDetailDto> getDetail(@PathVariable("post-id") Long postId) {

        PostDetailDto responseDto = postService.getDetail(postId);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(value = "/{post-id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPostComments(@PathVariable("post-id") Long postId, Pageable pageable) {

        var responseDto = commentService.getPostComments(pageable, postId);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PostInfoDto>> getLists(@RequestParam(value = "category", required = false) Category category, @PageableDefault(size = 20) Pageable pageable,
                                                      @RequestParam(value = "keyword", required = false) String keyword) {

        var responseDto = postService.getLists(category, pageable, keyword);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePostResponseDto> create(@RequestBody CreatePostRequestDto requestDto, @LoginMemberAnno LoginMemberDto loginMemberDto) {

        CreatePostResponseDto responseDto = postService.create(requestDto, loginMemberDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{post-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> put(@PathVariable("post-id") Long postId, @RequestBody CreatePostRequestDto requestDto, @LoginMemberAnno LoginMemberDto loginMemberDto) {

        postService.put(postId, requestDto, loginMemberDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{post-id}")
    public ResponseEntity<Void> delete(@PathVariable("post-id") Long postId, @LoginMemberAnno LoginMemberDto loginMemberDto) {

        postService.delete(postId, loginMemberDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{post-id}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCommentResponseDto> createComment(@RequestBody CreateCommentRequestDto requestDto, @LoginMemberAnno LoginMemberDto loginMemberDto, @PathVariable("post-id") Long postId) {

        CreateCommentResponseDto responseDto = commentService.create(requestDto, loginMemberDto, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{post-id}/comments/{comment-id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> putComment(@RequestBody CreateCommentRequestDto requestDto, @LoginMemberAnno LoginMemberDto loginMemberDto, @PathVariable("post-id") Long postId, @PathVariable("comment-id") Long commentId) {

        commentService.put(requestDto, loginMemberDto, postId, commentId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{post-id}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LikeResponseDto> createLike(@LoginMemberAnno LoginMemberDto loginMemberDto, @PathVariable("post-id") Long postId) {

        LikeResponseDto likeResponseDto = likeService.createPostLike(loginMemberDto, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDto);
    }

}
