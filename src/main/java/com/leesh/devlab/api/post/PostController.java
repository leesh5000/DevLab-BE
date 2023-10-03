package com.leesh.devlab.api.post;

import com.leesh.devlab.api.post.dto.PostInfo;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping(value = "/{post-id}")
    public ResponseEntity<PostInfo.Response> getDetail(@PathVariable("post-id") Long postId) {

        PostInfo.Response responseDto = postService.getDetail(postId);

        return ResponseEntity.ok(responseDto);

    }

    @PostMapping(value = "")
    public ResponseEntity<PostInfo.Response> create(@RequestBody PostInfo.Request requestDto, @LoginMember MemberInfo memberInfo) {

        PostInfo.Response responseDto = postService.create(requestDto, memberInfo);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

    }

    @PatchMapping(value = "/{post-id}")
    public ResponseEntity<Void> edit(@PathVariable("post-id") Long postId, @RequestBody PostInfo.Request requestDto, @LoginMember MemberInfo memberInfo) {

        postService.edit(postId, requestDto, memberInfo);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping(value = "/{post-id}")
    public ResponseEntity<Void> delete(@PathVariable("post-id") Long postId, @LoginMember MemberInfo memberInfo) {

        postService.delete(postId, memberInfo);

        return ResponseEntity.noContent().build();

    }

}
