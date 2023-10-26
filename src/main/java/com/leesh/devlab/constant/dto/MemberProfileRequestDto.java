package com.leesh.devlab.constant.dto;

import lombok.Builder;

@Builder
public record MemberProfileRequestDto(String nickname, Long createdAt, String introduce, ActivityDto activities) {

}
