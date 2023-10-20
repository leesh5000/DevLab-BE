package com.leesh.devlab.controller;

import com.leesh.devlab.config.LoginMember;
import com.leesh.devlab.dto.CommentDetail;
import com.leesh.devlab.dto.LikeInfo;
import com.leesh.devlab.jwt.dto.LoginInfo;
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
    public ResponseEntity<CommentDetail> getDetails(@PathVariable("comment-id") Long commentId) {

        return ResponseEntity.ok(commentService.getDetails(commentId));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDetail>> getLists(Pageable pageable) {

        return ResponseEntity.ok(commentService.getLists(pageable));
    }

    @DeleteMapping(value = "/{comment-id}")
    public ResponseEntity<Void> delete(@PathVariable("comment-id") Long commentId, @LoginMember LoginInfo loginInfo) {

        commentService.delete(commentId, loginInfo);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{comment-id}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LikeInfo> createLike(@LoginMember LoginInfo loginInfo, @PathVariable("comment-id") Long commentId) {

        LikeInfo likeInfo = likeService.createCommentLike(loginInfo, commentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeInfo);
    }

}
