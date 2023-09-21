package com.leesh.devlab.jwt.dto;

import com.leesh.devlab.constant.Role;
import com.leesh.devlab.domain.member.Member;

import java.util.Objects;

public record MemberInfo(Long id, String email, String name, Role role) {
    
    public MemberInfo {
        Objects.requireNonNull(id, "id must be provided.");
        Objects.requireNonNull(email, "email must be provided.");
        Objects.requireNonNull(name, "name must be provided.");
        Objects.requireNonNull(role, "role must be provided.");
    }

    public static MemberInfo from(Member member) {
        return new MemberInfo(member.getId(), member.getEmail(), member.getName(), member.getRole());
    }

}
