package com.leesh.devlab.exception;

import com.leesh.devlab.constant.ErrorCode;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *     애플리케이션 내에서 정의한 에러코드를 통해 API 응답으로 내보낼 DTO 객체
 * </p>
 * @param status
 * @param error
 */
public record ErrorResponse(HttpStatus status, Error error) {

    private record Error(String code, String description) {
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus(), new Error(errorCode.getCode(), errorCode.getDescription()));
    }

    public static ErrorResponse from(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode.getStatus(), new Error(errorCode.getCode(), createErrorMessage(bindingResult)));
    }

    public static ErrorResponse from(ErrorCode errorCode, Set<ConstraintViolation<?>> constraintViolations) {
        return new ErrorResponse(errorCode.getStatus(), new Error(errorCode.getCode(), createErrorMessage(constraintViolations)));
    }

    private static String createErrorMessage(BindingResult bindingResult) {

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (var fieldError : fieldErrors) {
            if (!isFirst) {
                sb.append(", ");
            } else {
                isFirst = false;
            }
            sb.append("[");
            sb.append(fieldError.getField());
            sb.append("] ");
            sb.append(fieldError.getDefaultMessage());
        }

        return sb.toString();
    }

    private static String createErrorMessage(Set<ConstraintViolation<?>> constraintViolations) {

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        for (ConstraintViolation<?> next : constraintViolations) {
            if (!isFirst) {
                sb.append(", ");
            } else {
                isFirst = false;
            }
            sb.append("[");
            sb.append(next.getPropertyPath());
            sb.append("] ");
            sb.append(next.getMessage());
        }

        return sb.toString();
    }

}
