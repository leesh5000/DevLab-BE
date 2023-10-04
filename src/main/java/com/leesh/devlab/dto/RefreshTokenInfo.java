package com.leesh.devlab.dto;

import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.TokenType;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record RefreshTokenInfo(GrantType grantType, Token accessToken) {

    public RefreshTokenInfo {

        Objects.requireNonNull(grantType, "grantType must be provided");
        Objects.requireNonNull(accessToken, "accessToken must be provided");

        if (!StringUtils.hasText(accessToken.getValue()) || !TokenType.isAccessToken(accessToken)) {
            throw new IllegalArgumentException("accessToken must be provided");
        }

    }

}
