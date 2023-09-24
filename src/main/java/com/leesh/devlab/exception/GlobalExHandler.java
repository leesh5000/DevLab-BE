package com.leesh.devlab.exception;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RestControllerAdvice
public class GlobalExHandler {

    /**
     * 비즈니스 로직 실행 중 오류 발생
     */
    @ExceptionHandler(value = { BusinessException.class })
    protected ResponseEntity<ErrorCode> handleBusinessException(BusinessException e) {

        log.error("[Business Exception]", e);

        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(errorCode);
    }

}
