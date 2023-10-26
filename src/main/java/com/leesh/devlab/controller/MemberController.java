package com.leesh.devlab.controller;

import com.leesh.devlab.config.LoginMemberAnno;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.service.CommentService;
import com.leesh.devlab.service.MemberService;
import com.leesh.devlab.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyProfileResponseDto> getMyProfile(@LoginMemberAnno LoginMemberDto loginMemberDto) {

        MyProfileResponseDto myProfileResponseDto = memberService.getMyProfile(loginMemberDto);

        return ResponseEntity.ok(myProfileResponseDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberProfileRequestDto> getProfile(@PathVariable("id") Long memberId) {

        MemberProfileRequestDto memberProfileRequestDto = memberService.getMemberProfile(memberId);
        
        return ResponseEntity.ok(memberProfileRequestDto);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@PathVariable("id") Long memberId,
                                              @LoginMemberAnno LoginMemberDto loginMemberDto,
                                              @RequestBody @Valid UpdateProfileRequestDto updateProfileRequestDto) {

        // 현재 로그인 한 사용자가 수정하려는 사용자와 같은지 확인
        isAccessible(memberId, loginMemberDto);

        memberService.updateProfile(memberId, updateProfileRequestDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("id") Long memberId,
                                             @LoginMemberAnno LoginMemberDto loginMemberDto) {

        // 현재 로그인 한 사용자가 자원을 수정하려는 사용자와 같은지 확인
        isAccessible(memberId, loginMemberDto);

        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PostInfoDto>> getMemberPosts(@PathVariable("id") Long memberId, @PageableDefault(size = 20) Pageable pageable) {

        Page<PostInfoDto> postPage = postService.getLists(pageable, memberId);

        return ResponseEntity.ok(postPage);
    }

    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDto>> getMemberComments(@PathVariable("id") Long memberId, @PageableDefault(size = 20) Pageable pageable) {

        Page<CommentDto> memberComments = commentService.getLists(pageable, memberId);

        return ResponseEntity.ok(memberComments);
    }

    private void isAccessible(Long memberId, LoginMemberDto loginMemberDto) {
        if (!Objects.equals(memberId, loginMemberDto.id())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "no permission");
        }
    }

}
