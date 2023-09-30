package com.leesh.devlab.api.post;

import com.leesh.devlab.api.post.dto.PostDto;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping(value = "")
    public ResponseEntity<PostDto.Response> createPost(@RequestBody PostDto.Request requestDto, @LoginMember MemberInfo memberInfo) {

        PostDto.Response responseDto = postService.createPost(requestDto, memberInfo);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

    }

}
