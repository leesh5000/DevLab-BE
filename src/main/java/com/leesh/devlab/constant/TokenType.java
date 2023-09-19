package com.leesh.devlab.constant;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS(60 * 20), // 20분
    REFRESH(60 * 60 * 24 * 7) // 7일
    ;

    // 토큰의 유효 기간
    private final Integer expiresIn;

    TokenType(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public static boolean isAccessToken(String tokenType) {
        return TokenType.ACCESS.name().equals(tokenType);
    }

    public static boolean isRefreshToken(String tokenType) {
        return TokenType.REFRESH.name().equals(tokenType);
    }

}
