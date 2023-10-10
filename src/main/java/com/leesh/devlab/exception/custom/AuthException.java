package com.leesh.devlab.exception.custom;

import com.leesh.devlab.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
