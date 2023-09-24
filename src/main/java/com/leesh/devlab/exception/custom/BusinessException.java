package com.leesh.devlab.exception.custom;

import com.leesh.devlab.constant.ErrorCode;
import lombok.Getter;

/**
 * <p>
 *     비즈니스 로직 중 발생하는 런타임 예외로 {@link ErrorCode}를 필드로 가지고 있으며, 이를 통해 세부 예외를 구분하고 각 예외 상황에 따른 대응 방안을 클라이언트에게 전달한다.
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
