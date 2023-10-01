package com.leesh.devlab.api.post;

import com.leesh.devlab.api.post.dto.PostDto;
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

    @PostMapping(value = "")
    public ResponseEntity<PostDto.Response> create(@RequestBody PostDto.Request requestDto, @LoginMember MemberInfo memberInfo) {

        PostDto.Response responseDto = postService.create(requestDto, memberInfo);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

    }

    @PatchMapping(value = "/{post-id}")
    public ResponseEntity<Void> edit(@PathVariable("post-id") Long postId, @RequestBody PostDto.Request requestDto, @LoginMember MemberInfo memberInfo) {

        postService.edit(postId, requestDto, memberInfo);

        return ResponseEntity.noContent().build();

    }

}
