package com.leesh.devlab.api.auth.dto;

import com.leesh.devlab.jwt.AuthToken;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.TokenType;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record RefreshTokenInfo(GrantType grantType, AuthToken accessToken) {

    public RefreshTokenInfo {

        Objects.requireNonNull(grantType, "grantType must be provided");
        Objects.requireNonNull(accessToken, "accessToken must be provided");

        if (!StringUtils.hasText(accessToken.getValue()) || !TokenType.isAccessToken(accessToken)) {
            throw new IllegalArgumentException("accessToken must be provided");
        }

    }

}
