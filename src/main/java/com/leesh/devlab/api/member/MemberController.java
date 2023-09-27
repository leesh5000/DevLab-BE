package com.leesh.devlab.api.member;

import com.leesh.devlab.api.member.dto.MemberProfile;
import com.leesh.devlab.api.member.dto.MyProfile;
import com.leesh.devlab.api.member.dto.UpdateProfile;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.MemberService;
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
    public ResponseEntity<MyProfile> getMyProfile(@LoginMember MemberInfo memberInfo) {

        MyProfile myProfile = memberService.getMyProfile(memberInfo);

        return ResponseEntity.ok(myProfile);
    }

    @GetMapping(value = "/{member-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberProfile> getProfile(@PathVariable("member-id") Long memberId) {

        MemberProfile memberProfile = memberService.getProfile(memberId);

        return ResponseEntity.ok(memberProfile);
    }

    @PatchMapping(value = "/{member-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@PathVariable("member-id") Long memberId,
                                              @LoginMember MemberInfo memberInfo,
                                              @RequestBody @Valid UpdateProfile updateProfile) {

        // 현재 로그인 한 사용자가 수정하려는 사용자와 같은지 확인
        isAccessible(memberId, memberInfo);

        memberService.updateProfile(memberId, updateProfile);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{member-id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("member-id") Long memberId,
                                             @LoginMember MemberInfo memberInfo) {

        // 현재 로그인 한 사용자가 자원을 수정하려는 사용자와 같은지 확인
        isAccessible(memberId, memberInfo);

        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{member-id}/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getMemberPosts(@PathVariable("member-id") Long memberId) {

        return null;
    }

    private void isAccessible(Long memberId, MemberInfo memberInfo) {
        if (!Objects.equals(memberId, memberInfo.id())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "no permission");
        }
    }


}
