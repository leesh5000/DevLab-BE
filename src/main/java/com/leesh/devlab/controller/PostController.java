package com.leesh.devlab.controller;

import com.leesh.devlab.config.LoginMember;
import com.leesh.devlab.domain.post.Category;
import com.leesh.devlab.dto.CreateComment;
import com.leesh.devlab.dto.CreatePost;
import com.leesh.devlab.dto.LikeInfo;
import com.leesh.devlab.dto.PostDetail;
import com.leesh.devlab.jwt.dto.LoginInfo;
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
    public ResponseEntity<PostDetail> getDetail(@PathVariable("post-id") Long postId) {

        PostDetail responseDto = postService.getDetail(postId);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PostDetail>> getLists(@RequestParam(value = "category", required = false) Category category, @PageableDefault(size = 20) Pageable pageable) {

        Page<PostDetail> responseDto = postService.getLists(category, pageable);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePost.Response> create(@RequestBody CreatePost.Request requestDto, @LoginMember LoginInfo loginInfo) {

        CreatePost.Response responseDto = postService.create(requestDto, loginInfo);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{post-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> put(@PathVariable("post-id") Long postId, @RequestBody CreatePost.Request requestDto, @LoginMember LoginInfo loginInfo) {

        postService.put(postId, requestDto, loginInfo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{post-id}")
    public ResponseEntity<Void> delete(@PathVariable("post-id") Long postId, @LoginMember LoginInfo loginInfo) {

        postService.delete(postId, loginInfo);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{post-id}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateComment.Response> createComment(@RequestBody CreateComment.Request requestDto, @LoginMember LoginInfo loginInfo, @PathVariable("post-id") Long postId) {

        CreateComment.Response responseDto = commentService.create(requestDto, loginInfo, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{post-id}/comments/{comment-id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> putComment(@RequestBody CreateComment.Request requestDto, @LoginMember LoginInfo loginInfo, @PathVariable("post-id") Long postId, @PathVariable("comment-id") Long commentId) {

        commentService.put(requestDto, loginInfo, postId, commentId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{post-id}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LikeInfo> createLike(@LoginMember LoginInfo loginInfo, @PathVariable("post-id") Long postId) {

        LikeInfo likeInfo = likeService.createPostLike(loginInfo, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeInfo);
    }

}
