package com.leesh.devlab.global.jwt;

import com.leesh.devlab.domain.member.constant.Role;

import java.util.Objects;

public record MemberInfo(Long id, String email, String name, Role role) {

    public MemberInfo {
        Objects.requireNonNull(id, "id must be provided.");
        Objects.requireNonNull(email, "email must be provided.");
        Objects.requireNonNull(name, "name must be provided.");
        Objects.requireNonNull(role, "role must be provided.");
    }

}
