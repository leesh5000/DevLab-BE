package com.leesh.devlab.constant.dto;

import com.leesh.devlab.constant.OauthType;

public record OauthLoginRequestDto(OauthType oauthType, String authorizationCode) {
}
