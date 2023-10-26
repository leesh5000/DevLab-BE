package com.leesh.devlab.constant.dto;

import com.leesh.devlab.jwt.Token;

public record TokenInfoDto(String grantType, Token accessToken, Token refreshToken) {
}
