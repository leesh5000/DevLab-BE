package com.leesh.devlab.exception;

import com.leesh.devlab.exception.custom.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RestControllerAdvice
public class GlobalExHandler {

    /**
     * {@link jakarta.validation.Valid} 또는 {@link org.springframework.validation.annotation.Validated} 예외를 처리하는 핸들러
     */
    @ExceptionHandler({BindException.class})
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {

        log.warn("[Binding Exception]", e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        ErrorResponse response = ErrorResponse.from(errorCode, e.getBindingResult());

        return ResponseEntity
                .status(response.status())
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {

        log.warn("[Constraint Violation Exception]", e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        ErrorResponse response = ErrorResponse.from(errorCode, constraintViolations);

        return ResponseEntity
                .status(response.status())
                .body(response);
    }

    /**
     * 비즈니스 로직 실행 중 발생하는 오류를 처리하는 핸들러
     * {@link BusinessException}
     */
    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        log.error("[Business Exception]", e);

        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.from(errorCode);

        return ResponseEntity
                .status(response.status())
                .body(response);
    }

}
