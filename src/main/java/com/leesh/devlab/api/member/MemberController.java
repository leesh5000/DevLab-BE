package com.leesh.devlab.api.member;

import com.leesh.devlab.api.member.dto.*;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyProfile> getMyProfile(@LoginMember LoginInfo loginInfo) {

        MyProfile myProfile = memberService.getMyProfile(loginInfo);

        return ResponseEntity.ok(myProfile);
    }

    @GetMapping(value = "/{member-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberProfile> getProfile(@PathVariable("member-id") Long memberId) {

        MemberProfile memberProfile = memberService.getMemberProfile(memberId);

        return ResponseEntity.ok(memberProfile);
    }

    @PatchMapping(value = "/{member-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@PathVariable("member-id") Long memberId,
                                              @LoginMember LoginInfo loginInfo,
                                              @RequestBody @Valid UpdateProfile updateProfile) {

        // 현재 로그인 한 사용자가 수정하려는 사용자와 같은지 확인
        isAccessible(memberId, loginInfo);

        memberService.updateProfile(memberId, updateProfile);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{member-id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("member-id") Long memberId,
                                             @LoginMember LoginInfo loginInfo) {

        // 현재 로그인 한 사용자가 자원을 수정하려는 사용자와 같은지 확인
        isAccessible(memberId, loginInfo);

        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{member-id}/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getMemberPosts(@PathVariable("member-id") Long memberId) {

        return null;
    }

    private void isAccessible(Long memberId, LoginInfo loginInfo) {
        if (!Objects.equals(memberId, loginInfo.id())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "no permission");
        }
    }

    @PostMapping(path = "/{member-id}/email/verify")
    public ResponseEntity<Void> emailVerify(@PathVariable("member-id") Long memberId, @LoginMember LoginInfo loginInfo, @RequestBody EmailVerify requestDto, HttpSession session) {

        isAccessible(memberId, loginInfo);

        memberService.emailVerify(requestDto, session);

        return ResponseEntity.noContent().build();
    }
    @PostMapping(path = "/{member-id}/email/confirm")
    public ResponseEntity<Void> emailConfirm(@PathVariable("member-id") Long memberId, @LoginMember LoginInfo loginInfo, @RequestBody EmailConfirm requestDto, HttpSession session) {

        isAccessible(memberId, loginInfo);

        memberService.emailConfirm(loginInfo, requestDto, session);

        return ResponseEntity.noContent().build();
    }


}
