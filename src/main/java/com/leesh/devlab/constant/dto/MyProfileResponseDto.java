package com.leesh.devlab.constant.dto;

import com.leesh.devlab.domain.member.Oauth;
import lombok.Builder;

@Builder
public record MyProfileResponseDto(Long id, String loginId, Oauth oauth, String nickname, Long createdAt, String securityCode, String introduce, ActivityDto activities) {

}
