package com.leesh.devlab.constant;

import com.leesh.devlab.jwt.AuthToken;

public enum TokenType {

    ACCESS(20 * 60 * 10000), // 20분
    REFRESH(7 * 24 * 60 * 60 * 1000) // 7일
    ;

    // 토큰의 유효 기간 (밀리 초)
    private final long expiresInMills;

    TokenType(Integer expiresIn) {
        this.expiresInMills = expiresIn;
    }

    public long getExpiresInMills() {
        return expiresInMills;
    }

    public static boolean isAccessToken(AuthToken authToken) {
        return TokenType.ACCESS == authToken.getTokenType();
    }

    public static boolean isRefreshToken(AuthToken authToken) {
        return TokenType.REFRESH == authToken.getTokenType();
    }

}
