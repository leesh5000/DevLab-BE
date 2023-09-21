package com.leesh.devlab.jwt.implementation;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.AuthToken;

import java.util.Objects;

public record Jwt(TokenType tokenType, String value, long expiresInMills) implements AuthToken {

    public Jwt {
        Objects.requireNonNull(tokenType, "tokenType must be not null");
        Objects.requireNonNull(value, "value must be not null");

        if (expiresInMills < System.currentTimeMillis()) {
            throw new IllegalArgumentException("expiresInMills must be greater than current time");
        }

    }

    @Override
    public TokenType getTokenType() {
        return this.tokenType;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    // 단위 : 밀리 초
    @Override
    public long getExpiresInMills() {
        return this.expiresInMills;
    }

}
