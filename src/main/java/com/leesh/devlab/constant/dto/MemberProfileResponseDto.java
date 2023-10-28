package com.leesh.devlab.constant.dto;

import lombok.Builder;

@Builder
public record MemberProfileResponseDto(Long id, String nickname, Long createdAt, String introduce, ActivityDto activities) {

}
