package com.leesh.devlab.global.jwt;

import com.leesh.devlab.domain.member.constant.Role;
import lombok.Builder;

@Builder
public record MemberInfo(Long id, String email, String name, Role role) {
}
