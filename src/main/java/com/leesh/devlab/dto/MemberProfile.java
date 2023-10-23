package com.leesh.devlab.dto;

import lombok.Builder;

@Builder
public record MemberProfile(String nickname, Long createdAt, String introduce, Activities activities) {

}
