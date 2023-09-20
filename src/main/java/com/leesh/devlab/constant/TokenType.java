package com.leesh.devlab.constant;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS(60 * 20), // 20분
    REFRESH(60 * 60 * 24 * 7) // 7일
    ;

    // 토큰의 유효 기간 (초)
    private final Integer expiresIn;

    TokenType(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

}
