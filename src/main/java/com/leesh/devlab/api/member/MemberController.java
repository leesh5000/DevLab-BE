package com.leesh.devlab.api.member;

import com.leesh.devlab.api.member.dto.MyProfile;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.leesh.devlab.constant.ErrorCode.NO_PERMISSION;

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
    public Void getProfile(@PathVariable("member-id") Long memberId, @LoginMember MemberInfo memberInfo) {

        // 접근 권한이 있는 사용자인지 검증
        isAccessible(memberId, memberInfo);
        return null;
    }

    private void isAccessible(Long memberId, MemberInfo memberInfo) {
        if (!Objects.equals(memberInfo.id(), memberId)) {
            throw new BusinessException(NO_PERMISSION, "no permission member.");
        }
    }


}
