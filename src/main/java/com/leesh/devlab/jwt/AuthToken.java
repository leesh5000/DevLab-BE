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
     * @return 토큰의 유효 기간 (초)
     */
    Integer getExpiresIn();

}
