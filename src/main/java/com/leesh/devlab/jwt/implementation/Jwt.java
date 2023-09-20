package com.leesh.devlab.jwt.implementation;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.AuthToken;

import java.util.Objects;

public record Jwt(TokenType tokenType, String value) implements AuthToken {

    public Jwt {
        Objects.requireNonNull(tokenType, "tokenType must be not null");
        Objects.requireNonNull(value, "value must be not null");
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
    public Integer getExpiresIn() {
        return switch (this.tokenType()) {
            case ACCESS -> TokenType.ACCESS.getExpiresIn();
            case REFRESH -> TokenType.REFRESH.getExpiresIn();
        };
    }
}
