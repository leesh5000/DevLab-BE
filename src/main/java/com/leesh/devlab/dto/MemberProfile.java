package com.leesh.devlab.dto;

import lombok.Builder;

@Builder
public record MemberProfile(Long id, String nickname, Long createdAt, Activities activities) {

}
