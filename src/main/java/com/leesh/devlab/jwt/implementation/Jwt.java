package com.leesh.devlab.jwt.implementation;

import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenType;

import java.util.Objects;

public record Jwt(TokenType tokenType, String value, int expiresInSeconds) implements Token {

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
    public int getExpiresInSeconds() {
        return this.expiresInSeconds;
    }

}
