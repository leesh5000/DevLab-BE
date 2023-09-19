package com.leesh.devlab.global.jwt.implementation;

import com.leesh.devlab.global.jwt.AuthToken;
import com.leesh.devlab.global.jwt.constant.TokenType;

import java.util.Objects;

public record Jwt(TokenType tokenType, String value) implements AuthToken {

    public Jwt {
        Objects.requireNonNull(tokenType, "tokenType must be provided.");
        Objects.requireNonNull(value, "value must be provided.");
    }

    @Override
    public TokenType getTokenType() {
        return this.tokenType;
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
