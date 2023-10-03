package com.leesh.devlab.api.member.dto;

import lombok.Builder;

@Builder
public record MyProfile(
        Long id, String loginId, String nickname, String email, Long createdAt,
        Activities activities) {

}
