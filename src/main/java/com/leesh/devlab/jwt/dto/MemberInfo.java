package com.leesh.devlab.jwt.dto;

import com.leesh.devlab.domain.member.Role;
import com.leesh.devlab.domain.member.Member;

import java.util.Objects;

public record MemberInfo(Long id, String nickname, Role role) {
    
    public MemberInfo {
        Objects.requireNonNull(id, "id must be provided.");
        Objects.requireNonNull(nickname, "nickname must be provided.");
        Objects.requireNonNull(role, "role must be provided.");
    }

    public static MemberInfo from(Member member) {
        return new MemberInfo(member.getId(), member.getNickname(), member.getRole());
    }

}
