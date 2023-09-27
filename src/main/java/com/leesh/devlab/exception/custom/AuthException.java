package com.leesh.devlab.exception.custom;

import com.leesh.devlab.constant.ErrorCode;

/**
 * 유저 인증 및 토큰 발급과 같이 서비스의 비즈니스 로직 이외에 인증과 관련된 부분에서 발행하는 예외
 */
public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
