package com.leesh.devlab.api.member.dto;

import lombok.Builder;

@Builder
public record MemberProfile(Long id, String nickname, Long createdAt, Activities activities) {

}
