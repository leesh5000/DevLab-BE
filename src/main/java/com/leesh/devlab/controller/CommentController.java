package com.leesh.devlab.controller;

import com.leesh.devlab.dto.CommentDetail;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping(value = "/comments/{comment-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDetail> getDetails(@PathVariable("comment-id") Long commentId) {

        return ResponseEntity.ok(commentService.getDetails(commentId));
    }

    @GetMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDetail>> getLists(Pageable pageable) {

        return ResponseEntity.ok(commentService.getLists(pageable));
    }

    @DeleteMapping(value = "/comments/{comment-id}")
    public ResponseEntity<Void> delete(@PathVariable("comment-id") Long commentId, @LoginMember LoginInfo loginInfo) {

        commentService.delete(commentId, loginInfo);

        return ResponseEntity.noContent().build();
    }



}
