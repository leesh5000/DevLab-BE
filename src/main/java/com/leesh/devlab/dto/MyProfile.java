package com.leesh.devlab.dto;

import lombok.Builder;

@Builder
public record MyProfile(Long id, String loginId, String nickname, Long createdAt, String securityCode, String introduce, Activities activities) {

}
