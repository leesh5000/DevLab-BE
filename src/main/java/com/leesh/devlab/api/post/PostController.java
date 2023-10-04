package com.leesh.devlab.api.post;

import com.leesh.devlab.api.post.dto.CreatePost;
import com.leesh.devlab.api.post.dto.PostDetail;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.resolver.LoginMember;
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

    @GetMapping(value = "/{post-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDetail> getDetail(@PathVariable("post-id") Long postId) {

        PostDetail responseDto = postService.getDetail(postId);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PostDetail>> getLists(@PageableDefault(size = 20) Pageable pageable) {

        Page<PostDetail> responseDto = postService.getLists(pageable);

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

}
