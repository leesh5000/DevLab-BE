package com.leesh.devlab.api.auth.dto;

import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.AuthToken;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record TokenRefreshDto(GrantType grantType, AuthToken accessToken) {

    public TokenRefreshDto {

        Objects.requireNonNull(grantType, "grantType must be provided");
        Objects.requireNonNull(accessToken, "accessToken must be provided");

        if (!StringUtils.hasText(accessToken.getValue()) || !TokenType.isAccessToken(accessToken)) {
            throw new IllegalArgumentException("accessToken must be provided");
        }

    }

}
