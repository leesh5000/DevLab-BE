package com.leesh.devlab.global.exception;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RestControllerAdvice
public class GlobalExHandler {

    private final MessageSourceAccessor messageSource;

    /**
     * {@link org.springframework.validation.annotation.Validated} 예외가 발생할 경우
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {

        log.error("handleBindException", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.toString(), e.getBindingResult());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생 혹은 @PathVariable 형식에 잘못된 타입을 넣었을 때
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {

        log.error("handleMethodArgumentTypeMismatchException", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.toString(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {

        log.error("handleHttpRequestMethodNotSupportedException", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.METHOD_NOT_ALLOWED.toString(), e.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    /**
     * 비즈니스 로직 실행 중 오류 발생
     */
    @ExceptionHandler(value = { BusinessException.class })
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        log.error("BusinessException", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode().getCode(),
                convertErrorMessage(e.getErrorCode().getCode()));

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 예외 처리하지 못한 나머지 예외 발생
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Exception", e);

        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode(),
                convertErrorMessage(errorCode.getCode()));

        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    private String convertErrorMessage(String code) {
        return messageSource.getMessage(code);
    }

}
