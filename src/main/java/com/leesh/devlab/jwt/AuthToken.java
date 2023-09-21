package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;

public interface AuthToken {

    /**
     * @return 인증 토큰의 타입
     */
    TokenType getTokenType();

    /**
     * @return 토큰 값
     */
    String getValue();

    /**
     * 토큰의 유효시간을 반환 (밀리 초)
     * @return 토큰 유효 시간
     */
    long getExpiresInMills();

}
