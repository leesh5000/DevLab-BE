package com.leesh.devlab.constant;

import com.leesh.devlab.jwt.Token;
import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS(20 * 60), // 20분
    REFRESH(7 * 24 * 60 * 60) // 7일
    ;

    // 토큰의 유효 기간 (초)
    private final int expiresInSeconds;
    private final long expiresInMillis;

    TokenType(Integer expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
        this.expiresInMillis = expiresInSeconds * 1000L;
    }

    public static boolean isAccessToken(Token token) {
        return TokenType.ACCESS == token.getTokenType();
    }

    public static boolean isRefreshToken(Token token) {
        return TokenType.REFRESH == token.getTokenType();
    }

}
