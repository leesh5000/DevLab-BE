package com.leesh.devlab.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * 비즈니스 로직 중
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

    /* Auth */
    EXPIRED_TOKEN(UNAUTHORIZED, "A-001", "Expired Token"),
    INVALID_TOKEN(UNAUTHORIZED, "A-002", "Invalid Token"),
    INVALID_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003", "Invalid Authorization Header"),
    NOT_EXIST_AUTHORIZATION(UNAUTHORIZED, "A-004", "Authorization is empty"),

    /* Member */
    NOT_EXIST_MEMBER(NOT_FOUND, "M-001", "Not Exist Member"),
    ALREADY_REGISTERED_MEMBER(CONFLICT, "M-002", "Already Registered Member"),
    WRONG_PASSWORD(UNAUTHORIZED, "M-003", "Wrong Password"),

    /* Common */
    INVALID_INPUT(BAD_REQUEST, "C-001", "Bad Request"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String description;

    ErrorCode(HttpStatus status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

}
