package com.leesh.devlab.jwt.dto;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.Role;

import java.util.Objects;

public record LoginInfo(Long id, String nickname, Role role) {
    
    public LoginInfo {
        Objects.requireNonNull(id, "id must be provided.");
        Objects.requireNonNull(nickname, "nickname must be provided.");
        Objects.requireNonNull(role, "role must be provided.");
    }

    public static LoginInfo from(Member member) {
        return new LoginInfo(member.getId(), member.getNickname(), member.getRole());
    }

}
