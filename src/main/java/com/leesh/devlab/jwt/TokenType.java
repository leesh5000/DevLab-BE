package com.leesh.devlab.jwt;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS(20 * 60), // 20분
    REFRESH(7 * 24 * 60 * 60) // 7일
    ;

    // 토큰의 유효 기간 (초)
    private final int expiresIn;

    public long getExpiresInMillis() {
        return this.expiresIn * 1000L;
    }

    TokenType(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public static boolean isAccessToken(Token token) {
        return TokenType.ACCESS == token.getTokenType();
    }

    public static boolean isRefreshToken(Token token) {
        return TokenType.REFRESH == token.getTokenType();
    }

}
