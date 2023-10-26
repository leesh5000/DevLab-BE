package com.leesh.devlab.constant.dto;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.constant.Role;

import java.util.Objects;

public record LoginMemberDto(Long id, String nickname, Role role) {
    
    public LoginMemberDto {
        Objects.requireNonNull(id, "id must be provided.");
        Objects.requireNonNull(nickname, "nickname must be provided.");
        Objects.requireNonNull(role, "role must be provided.");
    }

    public static LoginMemberDto from(Member member) {
        return new LoginMemberDto(member.getId(), member.getNickname(), member.getRole());
    }

}
