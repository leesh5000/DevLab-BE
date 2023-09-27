package com.leesh.devlab.jwt.implementation;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.AuthToken;

import java.util.Objects;

public record Jwt(TokenType tokenType, String value, long expiredAt) implements AuthToken {

    public Jwt {
        Objects.requireNonNull(tokenType, "tokenType must be not null");
        Objects.requireNonNull(value, "value must be not null");

        if (expiredAt <= System.currentTimeMillis()) {
            throw new IllegalArgumentException("expiredAt must be not expired");
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

    @Override
    public long getExpiredAt() {
        return this.expiredAt;
    }

}
