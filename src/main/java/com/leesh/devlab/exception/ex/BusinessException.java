package com.leesh.devlab.exception.ex;

import com.leesh.devlab.constant.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직 중 발생하는 런타임 예외들에 대해서 알맞은 에러 메세지를 응답으로 보내주기 위한 클래스
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
