package com.leesh.devlab.api.post;

import com.leesh.devlab.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<CreatePostDto.Response> createPost(@LoginUser UserInfo userInfo,
//                                                             @RequestBody @Valid CreatePostDto.Request request) {
//
//        CreatePostDto.Response body = postService.createPost(userInfo, request);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(body);
//
//    }


}
