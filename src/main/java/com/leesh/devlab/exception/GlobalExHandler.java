package com.leesh.devlab.exception;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RestControllerAdvice
public class GlobalExHandler {

    /**
     * {@link jakarta.validation.Valid} 또는 {@link org.springframework.validation.annotation.Validated} 예외를 처리하는 핸들러
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {

        log.error("[Bind Exception]", e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        ErrorResponse response = ErrorResponse.from(errorCode, e.getBindingResult());

        return ResponseEntity
                .status(response.status())
                .body(response);
    }


    /**
     * 비즈니스 로직 실행 중 발생하는 오류를 처리하는 핸들러
     * {@link BusinessException}
     */
    @ExceptionHandler(value = { BusinessException.class })
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        log.error("[Business Exception]", e);

        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.from(errorCode);

        return ResponseEntity
                .status(response.status())
                .body(response);
    }

}
