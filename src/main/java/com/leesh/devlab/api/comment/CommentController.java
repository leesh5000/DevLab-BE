package com.leesh.devlab.api.comment;

import com.leesh.devlab.api.comment.dto.CreateComment;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{post-id}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateComment.Response> create(@RequestBody CreateComment.Request requestDto, @LoginMember LoginInfo loginInfo, @PathVariable("post-id") Long postId) {

        CreateComment.Response responseDto = commentService.create(requestDto, loginInfo, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{comment-id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> put(@RequestBody CreateComment.Request requestDto, @LoginMember LoginInfo loginInfo, @PathVariable("post-id") Long postId, @PathVariable("comment-id") Long commentId) {

        commentService.put(requestDto, loginInfo, postId, commentId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{comment-id}")
    public ResponseEntity<Void> delete(@PathVariable("comment-id") Long commentId, @PathVariable("post-id") Long postId, @LoginMember LoginInfo loginInfo) {

        commentService.delete(commentId, postId, loginInfo);

        return ResponseEntity.noContent().build();
    }

}
